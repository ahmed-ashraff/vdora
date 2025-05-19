# Vdora

**Vdora** is a basic text search engine built on the classical **Vector Space Model**, featuring a client with a read-evaluate-print loop (REPL) interface for interactive document search using cosine similarity ranking.

---

## Installation Requirements

Before building or running this project, ensure your system has the following installed:

### 1. Java Development Kit (JDK)

You need **Java 21** or later.

- **Windows/Linux/macOS**: Download from [Adoptium](https://adoptium.net/) or install via your OS package manager.

### 2. Maven

Vdora uses **Apache Maven** for build configuration and dependency management.

- **Windows**: Install via [Chocolatey](https://chocolatey.org/) → `choco install maven`
- **Linux**:
    - Debian/Ubuntu: `sudo apt install maven`
    - Fedora: `sudo dnf install maven`
- Or download from [Maven’s official site](https://maven.apache.org/).

---

## Build and Run

### 1. Clone the repository

```bash
git clone https://github.com/ahmed-ashraff/vdora
```

### 2. Build the project
```bash
cd vdora
mvn exec:java
```

## License
This project is released under the Apache License 2.0

## Code Style
This project follows the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html).