# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## 1.1.4

### Changed

- Accept more varied empty proofs (now also accepts an empty map)

## 1.1.3

### Changed

- Disabled logging of all actuator requests. The default filter regex pattern is `.*/actuator/.*`. The expression can be
  customized by setting the `request.logging.uri-filter-pattern` property.

## 1.1.2

### Added

- sd-jwt holder binding proof in jwt format can now not be issued too long ago or too far in the future.

### Changed

- Provide securosys HSM Primus jce provider (no change necessary for user)

## 1.1.1

### Fixed

- Use separate pre-auth code instead of management id to get token

## 1.1.0

### Added

- Add new credential_metadata field to database, allowing for arbitrary vc metadata to be passed along. Using the first
  defined field - vct#integrity
-
- Extending prometheus export with metrics for build

### Changed

- Restrict issuer_metadata cryptographic_binding_methods_supported property to a predefined set of values. For the time
  being it is always did:jwk. Allowed
  values are defined in the [README under allowed config values](README.md#allowed-config-values)

### Fixed

- Updated Spring Boot Parent, fixing CVE-2024-50379

## 1.0.1

- Add optional endpoints to deliver vct, json-schema and oca.

## 1.0.0

- Initial Release
