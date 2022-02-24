# TOME

## Purpose
Tome is a configuration library that is designed to make it easy to configure your application regardless
of the type of configuration file format or source. Tome is used to unify configuration by treating each
source as registry of configuration properties. Sources are designed to be used independently and mixed/matched
as needed to create multi-layered configuration that come together to make applications easy to configure across
environments and teams.

If you want to learn more about using Tome see the [tome-examples](examples/README.md) module.

## Background
Tome descends from some other configuration libraries like [ee-config](https://github.com/chrisruffalo/ee-config) and 
[yyall](https://github.com/chrisruffalo/yyall). The lessons learned from those projects have lead to the realization
that there is no such thing as a one-size-fits-all configuration library. Experiences with cloud-native applications
have shown that being able to compose configuration from multiple sources is extremely important: especially the ability
to get variable resolution that works across sources.

## Design Philosophy
Tome is designed to get out of your way as you mix and match it with whatever other configuration you like. You can
use it to bootstrap your configuration, resolve Java properties, or chain load multiple sources. The overall idea is
to augment and provide without requiring you to buy in to the API. It is built on simple interfaces that can be extended
(or, just as importantly, discarded) at will.

This is not an opinionated framework other than in the way it mostly just uses Readers for everything. If you can
address your configuration source by providing a key-like value and expect a response or if you can make use of a
Reader then you can benefit from using Tome.

## Modules
* bom (`tome-bom`) - common bill of materials for external dependencies
* core (`tome-core`) - core logic for property loading, sources, and resolution
* bean (`tome-bean`) - specific logic for property resolution in java beans
* ee (`tome-ee-*`) - java/jakarta EE provider modules ([ee-config](https://github.com/chrisruffalo/ee-config) reborn!)
* yaml (`tome-yaml`) - yaml reader/compatibility
* sops (`tome-sops`) - reader compatible with [Mozilla SOPS](https://github.com/mozilla/sops)
* test (`tome-test`) - utilities used in test cases across all modules
* examples (`tome-examples`) - various examples on tome use cases 

## Features

### Use what you have
Tome is designed to work with what you have. Anything that can provide a simple key-value interface can be used as a
Source to lookup properties. On the implementation side you can provide transformers, prefixes, priority, and other
customizations to create your application's configurations. If your configuration is already a Java bean Tome can work
with that as well to add it as one of many sources.

Tome is also designed to layer on existing configuration. The [DirectiveReader](core/src/main/java/io/github/chrisruffalo/tome/core/directive/DirectiveReader.java) 
to implement directives that augment configuration. The [ResolvingReader](core/src/main/java/io/github/chrisruffalo/tome/core/resolver/ResolvingReader.java) can
be used to resolve tokens inside a file as it is being read. Using these together can bring new capabilities an application's configuration without even
having to change the configuration framework.

### Flexible variables
One of the major features of tome is the ability to use a simple variable system for flexible customization. The
variable system supports defaults as well as quoted literal strings inside of variables.

Take this for example:
```yaml
network:
  scheme: https
  port: 8443
  host: google.com

api:
  # resolves to "https://google.com:8443/video"
  video: "${network.scheme}://${network.host}:${network.port}/video"
  # resolves to "https://storage.google.com:8443"
  storage: "${network.scheme}://storage.${network.host}:${network.port}"
  # resolves to "https://api.google.com:8443/users" unless 'environment.endpoint' is provided
  user: "${network.scheme}://api.${network.host}:${network.port}/${environment.endpoint | 'users'}"
```

The above works by parsing the YAML into a bean and then using the bean as a bean source to the resolver. This can
be extended to any number of sources that have a structure that either maps them out as properties (flat style) or
as a hierarchy (like beans). This means that it is not specific to yaml or one configuration library but is available
for _any_ sort of provider that allows for addressing keys by name.

### Build configuration in stages
Tome allows the configuration for an application to be built in stages. Each source added to a configuration extends the
capability. Use system properties or classpath defaults to locate a Java properties file that is then used to find
configuration to resolve an endpoint that is used to pull down the rest of the configuration.

### Simple fragment include system
In order to support DRY (Don't Repeat Yourself) principles Tome provides an _extremely simple_ fragment inclusion system. 
Given a fragment you can include it in another file. Once all the `include` directives have been processed the file can
be read by whatever configuration engine and then used as a `Configuration` source.

Here is a simple example. The following fragment is in `fragments/alternate-smtp.yml`:
```yaml
override:
  host: smtp-alt.company.lan
  port: 443
```

The main file is:
```yaml
%{include fragments/alternate-smtp.yml}%

smtp:
  host: ${override.host | 'smtp.company.lan'}
  port: ${override.port | '25'}
```

This allows a simple mechanic to override files depending on another file. The path fragment supports variables
as well assuming that the DirectiveConfiguration has been set up with a Configuration that has sources for the
properties that need to be resolved.

### Variables from multiple sources
Since each configuration implementation is designed in such a way that it can be, itself, used a source. One of the
features that this enables is loading one type of configuration to "bootstrap" another. You could load a properties
file and then use the values in that property file to load or resolve further sources.

### Customizable token and prefix system
Tokens in Tome look like `${ token }` but they can be configured easily to support other token types. Tokens 
can be asymmetric (like the default) or symmetric (`@@ token @@`). Symmetric tokens cannot support nesting. Likewise
there is a separator character (default: `|`) between the parts of a token that can be configured as well.

Directives (tokens that change the operation of a file) are available as well. They generally work to include file
fragments but more features may be added in the future. The default directive token looks like `%{ command option1 option2 }%`
but it can be configured. Nested tokens may be available in the future for resolution based on bootstrap configuration. Spaces serve
as the separators between commands and options so any options withs paces should be quoted.