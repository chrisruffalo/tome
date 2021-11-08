# TOME

## Purpose
Tome is a configuration library that is designed to make it easy to configure your application regardless
of the type of configuration file format or source. Tome is used to unify configuration by treating each
source as registry of configuration properties. Sources are designed to be used independently and mixed/matched
as needed to create multi-layered configuration that come together to make applications easy to configure across
environments and teams.

## Background
Tome descends from some other configuration libraries like [ee-config](https://github.com/chrisruffalo/ee-config) and 
[yyall](https://github.com/chrisruffalo/yyall). The lessons learned from those projects have lead to the realization
that there is no such thing as a one-size-fits-all configuration library. Experiences with cloud-native applications
has show that being able to compose configuration from multiple sources is extremely important: especially the ability
to get variable resolution that works across sources.

## Features

### Flexible Variables
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
as a hierarchy (like beans). This means that it is not specific to yaml or one configuration libarary but is available
for _any_ sort of provider that allows for addressing keys by name.

### Simple fragment include system
In order to support non repeating yourself Tome provides an _extremely simple_ fragment inclusion system. Given
a fragment you can include it in another file. Once all the `include` directives have been processed the file can
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

### Flexible token and prefix system
By default tokens in Tome look like `${ token }` but they can be configured easily to support other token types. Tokens 
can be asymmetric (like the default) or symmetric (`@@ token @@`). Symmetric tokens cannot support nesting. Likewise
there is a separator character (defualt: `|`) between the parts of a token that can be configured as well.

Likewise directives (tokens that change the operation of a file) are available as well. They generally work to include file
fragments but more features may be added in the future. The default directive token looks like `%{ command option1 option2 }%`
but it can be configured. Nested tokens may be available in the future for resolution based on bootstrap configuration. Spaces serve
as the separators between commands and options so any options withs paces should be quoted.