package io.github.chrisruffalo.tome.sops;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

public class SopsReader extends Reader {

    private static final int BUFFER_SIZE = 1024 * 8; // 8kb buffer

    private static final String PATH_VAR = "PATH";

    private static final String SOPS_PATH_PROPERTY = "sops.executable_path";
    private static final String SOPS_ENV_PROPERTY = "SOPS_EXECUTABLE_PATH";

    private static final String COMMAND_INPUT_TYPE = "--input-type";
    private static final String COMMAND_OUTPUT_TYPE = "--output-type";

    private static final String[] EXTENSIONS = new String[]{"", ".exe"};
    private static final String SOPS_EXECUTABLE = "sops";
    private static final String SOPS_KEY_PREFIX = "sops.";

    private final SopsConfiguration configuration;
    private final Path pathToSops;
    private SopsDataType dataType;

    private Reader input;
    private Path inputPath;

    private volatile Reader delegate;

    private SopsReader(final SopsConfiguration configuration) {
        // check for the ability to find the sops binary on the path
        // and if it cannot be found
        Optional<Path> sopsPath = findSopsByConfigurationAndThenPath();
        if (!sopsPath.isPresent()) {
            throw new RuntimeException("Could not find `sops` executable on path");
        }

        // try and execute sops
        int exitValue = 0;
        try {
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(sopsPath.get().toString(), "--version");
            final Process process = builder.start();
            final AtomicReference<String> versionReference = new AtomicReference<>(null);
            new Thread(() -> {
                try (final BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while((line = reader.readLine()) != null) {
                        if(versionReference.get() == null && !line.isEmpty()) {
                            versionReference.set(line);
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException("Could not read sops version", e);
                }
            }).start();
            exitValue = process.waitFor();

            // can parse out version if we want to now
            //if (versionReference.get() != null) {
            //
            //}
        } catch (Exception e) {
            throw new RuntimeException("Could not execute `sops` command", e);
        }
        if (exitValue != 0) {
            throw new RuntimeException(String.format("There was an error executing the `sops --version` command, exit value=%d", exitValue));
        }

        // save configuration to instance
        this.configuration = configuration == null ? new SopsConfiguration() : configuration;

        // the sops executable has been found and works so use it
        pathToSops = sopsPath.get();
    }

    /**
     * Creates a sops reader that reads from the given reader and assumes
     * the default input type (JSON).
     *
     * @param input reader to read from
     */
    public SopsReader(Reader input) {
        this(input, SopsDataType.JSON);
    }

    /**
     * Creates a sops reader that assumes the provided input type
     *
     * @param input reader to read from
     * @param inputType the type (json, yaml, etc) of the input
     */
    public SopsReader(Reader input, final SopsDataType inputType) {
        this(new SopsConfiguration(), input, inputType);
    }

    /**
     * Creates a sops reader with a configuration assuming the default
     * input type (JSON).
     *
     * @param configuration the configuration object to use for configuring the sops execution environment
     * @param input reader to read from
     */
    public SopsReader(SopsConfiguration configuration, final Reader input) {
        this(configuration, input, SopsDataType.JSON);
    }

    /**
     * Fully configured sops reader that takes a configuration, reader input, and the
     * data type for input.
     *
     * @param configuration the configuration object to use for configuring the sops execution environment
     * @param input reader to read from
     * @param inputType the type (json, yaml, etc) of the input
     */
    public SopsReader(SopsConfiguration configuration, Reader input, final SopsDataType inputType) {
        this(configuration);

        // use this as the input when the sops command is invoked
        this.input = input;
        this.dataType = inputType;
    }

    /**
     * Create a sops reader pointing to an object at a path
     *
     * @param inputPath the path to the file
     */
    public SopsReader(final Path inputPath) {
        this(new SopsConfiguration(), inputPath);
    }

    /**
     * Create a sops reader with a configuration pointing at an object at a path
     *
     * @param configuration configuration object
     * @param inputPath the path to the file
     */
    public SopsReader(SopsConfiguration configuration, Path inputPath) {
        this(configuration);

        this.inputPath = inputPath;
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        // check if the delegate is null and if it is create a new one
        if (delegate == null) {
            synchronized (pathToSops) {
                if (delegate == null) {
                    invoke();
                }
            }
        }
        return delegate.read(cbuf, off, len);
    }

    @Override
    public void close() throws IOException {
        if (delegate != null) {
            delegate.close();
        }
    }

    /**
     * Invoke the found sops command on the input stream
     */
    private void invoke() {
        try {
            // write input reader to temporary file
            Path inputPath = this.inputPath;
            boolean createdTempFile = false;

            if (inputPath == null && this.input != null) {
                inputPath = Files.createTempFile("tome-sops-", "-temp");
                createdTempFile = true;
                try (
                    final Writer writer = Files.newBufferedWriter(inputPath)
                ) {
                    SopsReader.transfer(this.input, writer);
                }
            }

            if (inputPath == null) {
                throw new RuntimeException("No input provided for SOPS operation");
            }

            if (!Files.exists(inputPath)) {
                throw new RuntimeException("No input file exists for SOPS operation");
            }

            // create arguments
            final List<String> commandArgs = new ArrayList<>();
            commandArgs.add(pathToSops.toString());
            // if a data type is provided assume the pass-through and that the input/output type will be the same
            if (this.dataType != null) {
                // do not add command argument if it is provided in the configuration object
                if (!configuration.getCommandLine().containsKey(COMMAND_INPUT_TYPE)) {
                    commandArgs.add(COMMAND_INPUT_TYPE);
                    commandArgs.add(this.dataType.getArgument());
                }
                // do not add command argument if it is provided in the configuration object
                if (!configuration.getCommandLine().containsKey(COMMAND_OUTPUT_TYPE)) {
                    commandArgs.add(COMMAND_OUTPUT_TYPE);
                    commandArgs.add(this.dataType.getArgument());
                }
            }
            // go through other command keys provided by configuration
            this.configuration.getCommandLine().values().forEach(command -> {
                commandArgs.add(command.getKey());
                commandArgs.add(command.getStringValue());
            });
            commandArgs.add("-d");
            commandArgs.add(inputPath.toString());

            // build process from components
            final ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command(commandArgs);

            // copy all system properties that start with "sops." into environment
            System.getProperties().stringPropertyNames().forEach(key -> {
                if (key != null && key.startsWith(SOPS_KEY_PREFIX) && !SOPS_PATH_PROPERTY.equals(key)) {
                    processBuilder.environment().put(key.substring(SOPS_KEY_PREFIX.length()), System.getProperty(key));
                }
            });

            // copy environment
            processBuilder.environment().putAll(System.getenv());
            processBuilder.environment().remove(SOPS_ENV_PROPERTY);

            // use configuration to overwrite any of those values
            this.configuration.getEnvironment().values().forEach(environment -> {
                processBuilder.environment().put(environment.getKey(), environment.getStringValue());
            });

            final Process process = processBuilder.start();

            // collect stdout to delegate reader
            final StringBuilder outputBuffer = new StringBuilder();

            new Thread(() -> {
                try (
                    final BufferedReader processStream = new BufferedReader(new InputStreamReader(process.getInputStream()))
                ) {
                    String line;
                    while((line = processStream.readLine()) != null) {
                        outputBuffer.append(line);
                        outputBuffer.append(System.lineSeparator());
                    }
                } catch (IOException e) {
                    throw new RuntimeException("Could not read output from sops command", e);
                }
            }).start();

            final StringBuilder errorBuffer = new StringBuilder();
            new Thread(() -> {
                try (
                    final BufferedReader processStream = new BufferedReader(new InputStreamReader(process.getErrorStream()))
                ) {
                    String line;
                    while((line = processStream.readLine()) != null) {
                        errorBuffer.append(line);
                        errorBuffer.append(System.lineSeparator());
                    }
                } catch (IOException e) {
                    throw new RuntimeException("Could not read error stream from sops command", e);
                }
            }).start();

            final int exitValue = process.waitFor();
            if(exitValue != 0) {
               throw new RuntimeException(String.format("There was an error executing sops, exit value=%d, errors: %s", exitValue, errorBuffer));
            }

            this.delegate = new StringReader(outputBuffer.toString().trim());

            // clean up temporary file
            if (createdTempFile) {
                try {
                    Files.deleteIfExists(inputPath);
                } catch (Exception ex) {
                    // nothing we can do here
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not invoke sops decryption", e);
        }
    }

    /**
     * Looks through the path for the existence of the SOPS_EXECUTABLE with known extensions
     * and return the path if found.
     *
     * originally from https://stackoverflow.com/a/23539220
     *
     * @return optional containing the path if it is found, empty otherwise
     */
    private static Optional<Path> findSopsByConfigurationAndThenPath() {
        if (System.getProperties().containsKey(SOPS_PATH_PROPERTY)) {
            final String sopsSystemPropertyPath = System.getProperty(SOPS_PATH_PROPERTY);
            final Path candidatePath = Paths.get(sopsSystemPropertyPath);
            if(Files.exists(candidatePath)) {
                return Optional.of(candidatePath);
            }
        }

        if (System.getenv().containsKey(SOPS_ENV_PROPERTY)) {
            final String sopsEnvPath = System.getProperty(SOPS_PATH_PROPERTY);
            final Path candidatePath = Paths.get(sopsEnvPath);
            if(Files.exists(candidatePath)) {
                return  Optional.of(candidatePath);
            }
        }

        final String[] paths = System.getenv( PATH_VAR ).split(Pattern.quote(File.pathSeparator));
        for (String path : paths) {
            final Path candidateExecutable = Paths.get(path).resolve(SOPS_EXECUTABLE);

            for(final String extension : EXTENSIONS ) {
                final Path current = Paths.get(candidateExecutable + extension);
                if(Files.isExecutable(current)) {
                    return Optional.of(current);
                }
            }
        }

        return Optional.empty();
    }

    private static int transfer(final Reader input, final Writer output) throws IOException {
        char[] buffer = new char[BUFFER_SIZE];
        int count = 0;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }
}
