# StreamFlix

A small, original, Netflix-inspired movie catalog web application, built to teach the
**Java + Maven** fundamentals that later phases of this DevOps training project will build on
(GitHub Actions, AWS, Terraform, Elastic Beanstalk).

> **Phase 1 scope:** Create the app, run it locally, build it with Maven, test it, package it
> into a JAR, and run that JAR. Nothing here touches Git, GitHub, Docker, or AWS yet.

---

## 1. Project Overview

StreamFlix is a server-rendered Java web application that looks and feels like a simple movie
streaming homepage: a hero banner, rows of movie cards, a search box, a movie details page, and
an in-memory "My List" feature. It uses **no database, no external APIs, and no authentication
framework** — everything runs from a single Java process using data stored in memory.

The goal of this phase is not to build a production streaming service. It's to give you a real,
working Java + Maven application so you can learn (and later automate) the full build lifecycle:
compile → test → package → run.

## 2. What We Are Building

- An original application called **StreamFlix** (not affiliated with, and does not copy branding
  from, any real streaming service).
- A homepage with a header/navigation bar, a hero section, and several movie category rows.
- A movie details page.
- A simple keyword search over the movie catalog.
- An in-memory "My List" feature.
- A `/api/health` JSON endpoint, which will later be used by AWS Elastic Beanstalk to check that
  the app is alive.

## 3. Architecture

```text
Browser (you)
     |
     |  HTTP requests (GET /, GET /movie/1, GET /search?query=..., POST /movie/1/add-to-my-list)
     v
Spring Boot embedded web server (Apache Tomcat, port 8080)
     |
     v
Controllers (HomeController, MovieController, HealthController)
     |
     v
MovieService (in-memory list of Movie objects, plus an in-memory "My List")
     |
     v
Thymeleaf templates (HTML pages) rendered and sent back to the browser
```

There is no separate frontend build step and no database. The Java process itself serves HTML
pages (via Thymeleaf) and a JSON endpoint, all from one `.jar` file.

## 4. Technology Stack

| Layer               | Technology                          |
| ------------------- | ------------------------------------ |
| Language             | Java 21 (LTS)                        |
| Build tool           | Maven                                |
| Application framework | Spring Boot 3.5.3                  |
| Web layer            | Spring Web (Spring MVC)              |
| View templates       | Thymeleaf                            |
| Styling              | Plain CSS (no framework)             |
| Data storage         | In-memory Java objects (no database) |
| Testing              | Spring Boot Test, JUnit 5, AssertJ   |

## 5. Prerequisites

Before you start, you need a Windows laptop with:

1. Internet access (only needed the *first* time, to download Java, Maven, and Maven
   dependencies — after that, the app runs fully offline).
2. Permission to install software and edit environment variables.
3. About 2 GB of free disk space (JDK + Maven + downloaded dependencies).

## 6. Install Java on Windows

We recommend **Java 21 (LTS)**, e.g. the free [Eclipse Temurin](https://adoptium.net/) build.

1. Go to the Eclipse Temurin website and download the **JDK 21 (LTS)** Windows `.msi` installer
   (choose the x64 installer for a normal Windows PC).
2. Run the installer.
3. On the "Custom Setup" screen, make sure these options are enabled:
   - "Add to PATH"
   - "Set JAVA_HOME variable"
4. Finish the installation.

> Any current LTS release (21, or newer such as 25) will work with this project — the pom.xml
> targets Java 21 language/bytecode compatibility either way. Avoid non-LTS/short-term releases
> for a project like this.

## 7. Configure JAVA_HOME

If the installer didn't set it for you (or you installed the JDK as a `.zip`), set it manually:

1. Press `Win`, search for **"Environment Variables"**, and open
   **"Edit the system environment variables"**.
2. Click **Environment Variables...**
3. Under **System variables**, click **New...**
   - Variable name: `JAVA_HOME`
   - Variable value: the folder where the JDK was installed, e.g.
     `C:\Program Files\Eclipse Adoptium\jdk-21.0.x.x-hotspot`
4. Find the `Path` variable under **System variables**, click **Edit...**, click **New**, and add:
   ```text
   %JAVA_HOME%\bin
   ```
5. Click OK on every dialog to save.
6. Close and reopen any open PowerShell windows so they pick up the new variables.

## 8. Verify Java Installation

Open a **new** PowerShell window and run:

```powershell
java -version
```

You should see output similar to:

```text
openjdk version "21.0.x" ...
OpenJDK Runtime Environment Temurin-21.0.x+...
OpenJDK 64-Bit Server VM Temurin-21.0.x+... (mixed mode, sharing)
```

Also check:

```powershell
echo $env:JAVA_HOME
```

It should print the JDK install path.

## 9. Install Maven on Windows

1. Download the **Binary zip archive** from the
   [Apache Maven downloads page](https://maven.apache.org/download.cgi).
2. Extract it somewhere permanent, e.g. `C:\tools\apache-maven-3.9.x`.
   (Avoid folders that might get deleted later, like Downloads or Desktop.)

## 10. Configure Maven

1. Open **Environment Variables** the same way as in Step 7.
2. Under **System variables**, click **New...**
   - Variable name: `MAVEN_HOME`
   - Variable value: `C:\tools\apache-maven-3.9.x` (the folder you extracted)
3. Edit the `Path` variable again and add:
   ```text
   %MAVEN_HOME%\bin
   ```
4. Click OK on every dialog, then close and reopen PowerShell.

Remember the relationship:

```text
Java JDK
   |
   v
Java Compiler (javac)
   |
   v
Maven (uses javac + downloaded libraries to build your project)
   |
   v
Your Java Application (compiled classes + JAR)
```

**Maven requires Java** — Maven itself is a Java program, and it also invokes the Java compiler
to build your code. If Java isn't installed and on the `PATH` first, Maven will not work.

## 11. Verify Maven Installation

```powershell
mvn -version
```

Expected output looks like:

```text
Apache Maven 3.9.x (...)
Maven home: C:\tools\apache-maven-3.9.x
Java version: 21.0.x, vendor: Eclipse Adoptium, runtime: C:\Program Files\Eclipse Adoptium\jdk-21...
OS name: "windows 11", version: "10.0", arch: "amd64", family: "windows"
```

If the "Java version" line shows a real JDK version, Maven and Java are correctly linked.

## 12. Install Visual Studio Code

1. Download VS Code from https://code.visualstudio.com/.
2. Run the installer, accepting the defaults (make sure "Add to PATH" is checked).

## 13. Install Required VS Code Extensions

Open VS Code and install these extensions (Extensions icon on the left sidebar, or `Ctrl+Shift+X`):

- **Extension Pack for Java** (by Microsoft) — Java language support, debugging, project view.
- **Spring Boot Extension Pack** (by VMware/Microsoft) — Spring Boot-aware run/debug support.
- **Maven for Java** (usually bundled with the Java Extension Pack).

## 14. Clone/Open the Project

For Phase 1, there is no Git repository yet — you already have the project folder locally.

1. Open VS Code.
2. `File > Open Folder...`
3. Select the `MavenProjectWithAWSandGHA` project folder (the one containing `pom.xml`).

## 15. Understand the Maven Project Structure

```text
streamflix/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/streamflix/
│   │   │       ├── StreamFlixApplication.java     <- main() entry point
│   │   │       ├── controller/
│   │   │       │   ├── HomeController.java         <- handles "/"
│   │   │       │   ├── MovieController.java        <- handles /movie/{id}, /search, /my-list
│   │   │       │   └── HealthController.java       <- handles /api/health
│   │   │       ├── model/
│   │   │       │   └── Movie.java                  <- plain Java object for one movie
│   │   │       └── service/
│   │   │           └── MovieService.java           <- in-memory movie catalog + My List
│   │   └── resources/
│   │       ├── templates/                          <- Thymeleaf HTML pages
│   │       ├── static/css/style.css                <- dark theme styling
│   │       └── application.properties               <- server.port and other config
│   └── test/
│       └── java/com/streamflix/                    <- automated tests
├── .gitignore
├── pom.xml                                          <- Maven build definition
└── README.md
```

`src/main/java` is Maven's standard location for application source code.
`src/test/java` is Maven's standard location for test code.
`src/main/resources` holds non-Java files that get bundled into the JAR (templates, CSS,
config).

## 16. Understand pom.xml

`pom.xml` ("Project Object Model") is Maven's configuration file. Ours declares:

- **Parent**: `spring-boot-starter-parent` version `3.5.3` — gives us sensible default versions
  for all Spring-related dependencies, so we don't have to pick each one manually.
- **groupId**: `com.streamflix` — like a company/namespace identifier.
- **artifactId**: `streamflix` — the project's name; also used in the output JAR's filename.
- **version**: `1.0.0` — the project's version.
- **java.version**: `21` — tells Maven which Java language/bytecode level to compile against.
- **Dependencies**:
  - `spring-boot-starter-web` — gives us the embedded web server and Spring MVC (controllers).
  - `spring-boot-starter-thymeleaf` — gives us the Thymeleaf templating engine.
  - `spring-boot-starter-test` — gives us JUnit 5, AssertJ, MockMvc, etc. (test-only scope).
- **Build plugin**:
  - `spring-boot-maven-plugin` — repackages the compiled code into a single executable
    ("fat") JAR that includes an embedded Tomcat server, so you can run it with
    `java -jar` without installing a separate application server.

A **dependency** is simply a library that your project needs, which Maven downloads
automatically from a shared repository (Maven Central) instead of you downloading `.jar` files
by hand.

## 17. Run the Application Using Maven

From the project's root folder (where `pom.xml` lives) in PowerShell:

```powershell
mvn spring-boot:run
```

Wait for a line like:

```text
Started StreamFlixApplication in 2.5 seconds
```

The app is now running. Leave this terminal window open — press `Ctrl+C` in it to stop the app.

## 18. Access the Application

Open a browser and go to:

```text
http://localhost:8080
```

You should see the StreamFlix homepage with a hero section and movie rows. Click a movie card
to view its details page, try the search box, and click "Add to My List" then visit
`http://localhost:8080/my-list`.

## 19. Test the Health Endpoint

With the app still running, open:

```text
http://localhost:8080/api/health
```

You should see:

```json
{"status":"UP","application":"StreamFlix"}
```

## 20. Maven Commands

| Command                                 | Purpose                                                   |
| ---------------------------------------- | ---------------------------------------------------------- |
| `mvn clean`                              | Deletes the `target/` folder (removes previous build output). |
| `mvn compile`                            | Compiles the Java source code in `src/main/java`.          |
| `mvn test`                               | Compiles and runs all automated tests in `src/test/java`.  |
| `mvn package`                            | Runs tests, then bundles everything into an executable JAR in `target/`. |
| `mvn clean package`                      | Cleans first, then packages — the most common "give me a fresh JAR" command. |
| `mvn spring-boot:run`                    | Compiles and runs the app directly through Maven, without producing a JAR. |
| `java -jar target/streamflix-1.0.0.jar`  | Runs the already-built JAR directly with Java (no Maven needed at this point). |

In plain language: `clean` tidies up, `compile` turns your `.java` files into `.class` files,
`test` proves your code works the way you expect, `package` produces something shippable, and
`spring-boot:run` / `java -jar` are two different ways to actually start the running application.

## 21. Compile the Application

```powershell
mvn compile
```

This only turns your `.java` source files into `.class` bytecode files under
`target/classes`. It does **not** run tests and does **not** produce a JAR.

## 22. Run Tests

```powershell
mvn test
```

This project includes tests that check:

1. The full Spring application context starts up without errors.
2. `GET /api/health` returns HTTP 200 with the expected JSON body.
3. `MovieService` returns the full in-memory catalog (12 movies).
4. Searching by title (e.g. "Quantum") and by genre (e.g. "Action") returns the correct movies,
   and a search with no matches returns an empty list.

You should see a summary like:

```text
Tests run: 6, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

## 23. Package the Application

```powershell
mvn clean package
```

This compiles the code, runs all tests, and — if everything passes — produces an executable JAR
at:

```text
target/streamflix-1.0.0.jar
```

If any test fails, packaging stops and no JAR is produced. This is intentional: Maven won't let
you ship code that fails its own tests.

## 24. Understand the target Directory

After running `compile`, `test`, or `package`, Maven creates a `target/` folder:

```text
target/
├── classes/                          <- compiled application .class files + copied resources
├── test-classes/                     <- compiled test .class files
├── surefire-reports/                 <- detailed test result reports
├── streamflix-1.0.0.jar              <- the final executable JAR (after `package`)
└── streamflix-1.0.0.jar.original     <- the plain (non-executable) JAR before Spring Boot repackaged it
```

`target/` is entirely **generated** — everything in it can be recreated at any time by running
`mvn clean package` again. Because of this, it should **not** be committed to Git, which is why
our `.gitignore` excludes it:

```text
target/
```

## 25. Run the JAR Directly

Once you've packaged the app, you can run it with only Java (Maven is not required for this
step):

```powershell
java -jar target/streamflix-1.0.0.jar
```

Visit `http://localhost:8080` again to confirm it works the same way as `mvn spring-boot:run`.
Press `Ctrl+C` in the terminal to stop it.

## 26. Clean the Project

```powershell
mvn clean
```

This deletes the `target/` folder entirely. Your source code (`src/`) is never touched — only
generated build output is removed. Run this whenever you want a guaranteed fresh build.

## 27. Troubleshooting

### "'java' is not recognized as an internal or external command"

Java isn't installed, or it isn't on your `PATH`.

1. Confirm the JDK is installed (Step 6).
2. Confirm `JAVA_HOME` is set and `%JAVA_HOME%\bin` is in `Path` (Step 7).
3. Close **all** PowerShell windows and open a new one (environment variable changes don't apply
   to already-open terminals).
4. Re-run `java -version`.

### "'mvn' is not recognized as an internal or external command"

Maven isn't installed, or it isn't on your `PATH`.

1. Confirm Maven was extracted somewhere permanent (Step 9).
2. Confirm `MAVEN_HOME` is set and `%MAVEN_HOME%\bin` is in `Path` (Step 10).
3. Open a new PowerShell window and re-run `mvn -version`.
4. If `mvn -version` shows a Java error, fix Java first — Maven cannot run without it.

### Port 8080 already in use

If you see an error like `Web server failed to start. Port 8080 was already in use`, another
program (maybe a previous run of StreamFlix) is already listening on that port.

**Option A — find and stop the process using PowerShell:**

```powershell
netstat -ano | findstr :8080
```

This prints a line ending in a PID (process ID) number. Then stop it:

```powershell
taskkill /PID <the_pid_number> /F
```

**Option B — temporarily use a different port.** Edit
`src/main/resources/application.properties`:

```properties
server.port=8081
```

Then access the app at `http://localhost:8081` instead. Change it back to `8080` afterward if you
want to match this README exactly.

### Maven dependency download failure

If `mvn compile`/`test`/`package` fails with errors mentioning `Could not resolve dependencies`,
`Connection timed out`, or similar:

1. Check your internet connection (Maven needs to download dependencies from Maven Central the
   first time; after that they're cached locally in `~/.m2/repository`).
2. If you're on a corporate network, you may be behind a proxy — ask your network admin whether
   Maven needs proxy settings in `%USERPROFILE%\.m2\settings.xml`.
3. Try again — Maven Central occasionally has brief slowdowns.

### Build failure

If `mvn clean package` fails:

1. Scroll up in the terminal output to the first line containing `ERROR` or `BUILD FAILURE` —
   Maven prints failures top-to-bottom, and the *first* one is usually the real cause.
2. A `[ERROR] ... COMPILATION ERROR` means there's a mistake in a `.java` file — the message
   includes the file name and line number.
3. A test failure shows which test method failed and why (an assertion mismatch, an exception,
   etc.) — check `target/surefire-reports/` for full details on any failing test.
4. Fix the reported issue, save the file, and re-run `mvn clean package`.

## 28. Project Limitations

This is a **Phase 1 learning project**, intentionally simplified:

- **No database** — all movie data is hardcoded in `MovieService.java`.
- **"My List" is in-memory only** — it is shared by anyone using the app while it's running, and
  it **resets completely every time the application restarts**. There is no per-user login, so
  everyone sees the same "My List".
- **No authentication** — there are no user accounts or passwords.
- **No HTTPS** — the app runs over plain HTTP on `localhost`, which is fine for local learning
  but not for production use.
- **Posters are CSS gradients, not real images** — this keeps the app fully offline-capable and
  avoids any copyright concerns with real movie artwork.
- **Not yet deployed anywhere** — later phases will add GitHub, GitHub Actions, Terraform, and
  AWS Elastic Beanstalk deployment.

## 29. Phase 1 Completion Checklist

- [x] Application created (Spring Boot + Thymeleaf, no database).
- [x] Runs locally with `mvn spring-boot:run`.
- [x] `mvn clean` succeeds.
- [x] `mvn compile` succeeds.
- [x] `mvn test` succeeds (all automated tests pass).
- [x] `mvn package` / `mvn clean package` succeeds and produces `target/streamflix-1.0.0.jar`.
- [x] The JAR runs directly with `java -jar target/streamflix-1.0.0.jar`.
- [x] Homepage loads at `http://localhost:8080`.
- [x] Health endpoint works at `http://localhost:8080/api/health`.
- [x] Search works, e.g. `http://localhost:8080/search?query=Action`.
- [x] Movie details page works, e.g. `http://localhost:8080/movie/1`.
- [x] "My List" add/view flow works.
- [x] `.gitignore` created (excludes `target/` and other generated files).
- [x] README.md documents the entire setup for a fresh Windows laptop.
- [ ] Git repository initialized (intentionally **not** done yet — see Section 30).

## 30. Future DevOps Phases

This project is only Phase 1 of a larger pipeline. Coming next:

```text
Phase 1 (this document)          Phase 2 and beyond
------------------------         --------------------------------
Java Application                 Push the Maven application to GitHub
Maven                                    |
Run Locally                              v
Build & Test                     GitHub Actions (automated build & test)
                                          |
                                          v
                                  AWS Authentication
                                          |
                                          v
                                  Terraform (infrastructure as code)
                                          |
                                          v
                                  AWS Elastic Beanstalk
                                          |
                                          v
                                  Deploy Java Application
```

None of the later phases (Git, GitHub, GitHub Actions, AWS, Terraform, Docker, Elastic
Beanstalk) are configured yet. This project remains, for now, a clean, local, self-contained
Maven application.
