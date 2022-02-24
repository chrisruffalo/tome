# Tome SOPS

## Overview

The Tome SOPS is written to be compatible with [Mozilla SOPS](https://github.com/mozilla/sops) 
encrypted file sources. This is accomplished by calling out to the Mozilla SOPS binary (`sops`) and
decrypting the values using that.

For more information see the Mozilla SOPS project.

## Building

This module is not active by default but can be enabled with `mvn ... -Psops`. This is to allow for adding
the SOPS executable to the path before running this module.

## Requirements

This module requires the installation of the sops binary and the ability to find the sops binary on the
path of the system that is executing the consumer of this module. Alternately you can set the path to the
executable by setting the system property `sops.executable_path` or the environment variable `SOPS_EXECUTABLE_PATH`.
These require the full path to the executable itself (not the directory). The system property has precedence
over the environment variable if both are configured.

## Configuration

When `sops` is executed by this module it does pass through any configured environment variables to the 
sub-process so that any environment configuration necessary is passed on. Values can also be set as
system properties with the `sops.` prefix and they will be converted to environment variables. For example:

```bash
[]$ java -Dsops.SOPS_AGE_KEY_FILE ...  # when sops is called the env key SOPS_AGE_KEY_FILE will be set
```

Since this configuration is pass-through it should obey any and all configuration options that the underlying version
of Mozilla SOPS supports.

## Usage

### Basic

If the environment variables or other properties of sops have been set correctly, usage is simple:
```java
final Path input = Paths.get("/path/to/configuration.yml");
final YamlSource source = new YamlSource();
try (final SopsReader reader = new SopsReader(Files.newBufferedReader(input), SopsDataType.YAML)) {
    source.load(reader);
}
```

### Composing Sources

Because of the way that SOPS works directives/other information should be avoided within the encrypted
files and that source should be composed distinctly/separately. Take a look at the `testComposition` test
case in [SopsReaderTest](src/test/java/io/github/chrisruffalo/tome/sops/SopsReaderTest.java) to see how an
encrypted file can be overlain on the configuration to allow shared configuration files to selectively
reference encrypted values. The same could be accomplished by adding the encrypted file at a higher
priority but this method displays explicit preference.
