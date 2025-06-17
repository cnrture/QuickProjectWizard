# QuickProjectWizard

![Build](https://github.com/cnrture/QuickProjectWizard/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/25221.svg)](https://plugins.jetbrains.com/plugin/25221)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/25221.svg)](https://plugins.jetbrains.com/plugin/25221)

<img src="src/main/resources/META-INF/pluginIcon.svg" alt="New Project" width="144">

<!-- Plugin description -->

#### This plugin simplifies the process of setting up a new Android project with all the necessary dependencies and configurations. It also provides powerful development tools for managing modules, features, and project resources.
- Popular libraries and frameworks
- Compose, XML and Compose Multiplatform templates
- data, di, domain, ui, and common packages
- Built-in development tools and utilities
<!-- Plugin description end -->

## Installation

- Using the IDE built-in plugin system:
  
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "QuickProjectWizard"</kbd> >
  <kbd>Install</kbd>
  
- Using JetBrains Marketplace:

  Go to [JetBrains Marketplace](https://plugins.jetbrains.com/plugin/25221-quickprojectwizard) and install it by clicking the <kbd>Install to ...</kbd> button in case your IDE is running.

  You can also download the [latest release](https://plugins.jetbrains.com/plugin/25221-quickprojectwizard/versions) from JetBrains Marketplace and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

- Manually:

  Download the [latest release](https://github.com/cnrture/QuickProjectWizard/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

# Usage

## 1. Create a new project

### Compose and XML
<img src="images/new_project.png" alt="New Project">

### Compose Multiplatform
<img src="images/new_project_cmp.png" alt="New Project">

- Open your Android Studio.
- Click New Project.
- Select Quick Project Wizard and click Next.
</br></br>

## 2. Configure your project

### Compose and XML
<img src="images/details.png" alt="Quick Project Wizard Page">

### Compose Multiplatform
<img src="images/details_cmp.png" alt="New Project">

- Select the libraries, dependencies and screens you want to include in your project (for example, Retrofit, Room, Hilt, etc.).
- Customize versions and additional configurations.
- After all selections have been made, click Finish.
</br></br>

## 3. Finalize and start coding

### Compose and XML
<img src="images/final.png" alt="Quick Project Wizard Page">

### Compose Multiplatform
<img src="images/final_cmp.png" alt="New Project">

- Review the generated project structure to ensure all necessary dependencies and configurations are correctly applied.
- Start coding your new project with all the essential tools and libraries already integrated.
- Congratulations! You have successfully installed and configured the Quick Project Wizard plugin. Your new Android project is now set up with the latest and most essential dependencies, making your development process more efficient and streamlined.

## 4. Configure edit configurations (Just Compose Multiplatform)

<img src="images/edit_configurations.png" alt="Quick Project Wizard Page">

- Open edit configurations.

<img src="images/desktopApp_configuration.png" alt="Quick Project Wizard Page">

- Write your composeApp path instead of "\${PROJECT_DIR}\$" (like : ~/Documents/GitHub/CMPApp/composeApp)

<img src="images/iosApp_configuration.png" alt="Quick Project Wizard Page">

- Select Apple device from Execution Target.

## Development Tools

QuickProjectWizard includes a powerful tool window with several utilities to enhance your development workflow. Access
it via **View > Tool Windows > QuickProjectWizard**.

### Module Creator

Create new modules in your existing Android project with ease:

- **New Module**: Generate clean modules with customizable templates
- **Move Existing Files**: Reorganize existing code into proper module structure
- Automatic dependency detection and configuration
- Support for different module types (Library, Application, etc.)
- Template-based code generation

### Feature Creator

Rapidly scaffold new features for your project:

- Generate complete feature structures with data, domain, and UI layers
- Customizable file templates
- Interactive file tree browser for selecting target directories
- Consistent architecture patterns across features

### Color Picker

Professional color picking tool for UI development:

- System-wide color picker with crosshair cursor
- Magnified preview for precise color selection
- Color history with recent picks
- Copy colors in HEX and RGB formats
- One-click clipboard integration

### Settings Management

Comprehensive plugin configuration:

- Customize default package names and module preferences
- Import/Export settings for team collaboration
- Template management for modules and features
- Persistent configuration across IDE sessions

---
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
[docs:plugin-description]: https://plugins.jetbrains.com/docs/intellij/plugin-user-experience.html#plugin-description-and-presentation
