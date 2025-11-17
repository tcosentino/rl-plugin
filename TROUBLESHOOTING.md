# Test Harness Troubleshooting Guide

This guide helps you diagnose and resolve common issues when using the Objective Tracker test harness.

## Table of Contents

1. [Cannot Start Test Harness](#cannot-start-test-harness)
2. [Compilation Errors](#compilation-errors)
3. [UI Issues](#ui-issues)
4. [Data Loading Problems](#data-loading-problems)
5. [Performance Issues](#performance-issues)
6. [Export/Copy Features Not Working](#exportcopy-features-not-working)
7. [Keyboard Shortcuts Not Working](#keyboard-shortcuts-not-working)
8. [Getting Help](#getting-help)

---

## Cannot Start Test Harness

### Problem: `./gradlew runTestHarness` fails

**Symptoms:**
- Command not found
- Permission denied
- Gradle wrapper errors

**Solutions:**

1. **Make gradlew executable:**
   ```bash
   chmod +x gradlew
   ```

2. **If Gradle wrapper is missing:**
   ```bash
   gradle wrapper
   ./gradlew runTestHarness
   ```

3. **Use Java directly from your IDE:**
   - Right-click on `TestHarnessRunner.java`
   - Select "Run 'TestHarnessRunner.main()'"

### Problem: "Failed to initialize test harness" error

**Symptoms:**
- Error dialog appears on startup
- Application crashes immediately

**Solutions:**

1. **Check Java version:**
   ```bash
   java -version
   ```
   - Ensure Java 11 or higher is installed

2. **Check shop database file exists:**
   ```bash
   ls src/main/resources/com/questnextaction/data/shops.json
   ```

3. **Review logs for specific error:**
   - Check console output for stack traces
   - Look for messages about missing files or initialization failures

---

## Compilation Errors

### Problem: "Cannot resolve symbol" errors

**Symptoms:**
- Red underlines in IDE
- Compilation failures
- Missing imports

**Solutions:**

1. **Refresh Gradle dependencies:**
   ```bash
   ./gradlew clean build --refresh-dependencies
   ```

2. **Reimport project in IDE:**
   - IntelliJ IDEA: File → Invalidate Caches / Restart
   - Eclipse: Right-click project → Gradle → Refresh Gradle Project

3. **Check RuneLite dependencies:**
   - Ensure `build.gradle` has correct RuneLite version
   - Verify repository URLs are accessible

### Problem: Lombok annotations not working

**Symptoms:**
- `@Slf4j` not recognized
- Getter/setter methods not found

**Solutions:**

1. **Enable annotation processing in IDE:**
   - IntelliJ IDEA: Settings → Build → Compiler → Annotation Processors → Enable
   - Eclipse: Install Lombok plugin

2. **Verify Lombok dependency:**
   ```gradle
   compileOnly 'org.projectlombok:lombok:1.18.30'
   annotationProcessor 'org.projectlombok:lombok:1.18.30'
   ```

---

## UI Issues

### Problem: Window doesn't appear

**Symptoms:**
- Application starts but no window shows
- Process running but nothing visible

**Solutions:**

1. **Check if window is off-screen:**
   - Delete any saved window preferences
   - Window should center on screen by default

2. **Try different display settings:**
   - Check for multi-monitor issues
   - Verify display scaling settings

3. **Look for error messages in console:**
   - SwingUtilities exceptions
   - AWT/Graphics initialization errors

### Problem: Split pane doesn't resize properly

**Symptoms:**
- Divider stuck in one position
- Panels don't resize with window

**Solutions:**

1. **Drag the divider manually:**
   - Click and drag the vertical divider bar
   - Default position is 350 pixels from left

2. **Resize window:**
   - Maximize and restore window
   - This should reset layout

### Problem: Text is too small or too large

**Symptoms:**
- Debug text area font size issues
- UI elements scaled incorrectly

**Solutions:**

1. **Check display scaling:**
   - Windows: Display Settings → Scale
   - macOS: System Preferences → Displays

2. **Modify constants in code:**
   ```java
   private static final int FONT_SIZE = 12; // Adjust this value
   ```

---

## Data Loading Problems

### Problem: "0 shops loaded" in status bar

**Symptoms:**
- Shop database shows 0 shops
- Cannot add objectives
- Empty shop dropdown

**Solutions:**

1. **Verify shops.json exists:**
   ```bash
   ls -l src/main/resources/com/questnextaction/data/shops.json
   ```

2. **Check JSON file is valid:**
   - Open in text editor
   - Validate JSON syntax at jsonlint.com
   - Look for trailing commas or syntax errors

3. **Check file permissions:**
   ```bash
   chmod 644 src/main/resources/com/questnextaction/data/shops.json
   ```

### Problem: Sample objectives not loading

**Symptoms:**
- Debug panel shows "No objectives found"
- Empty objectives list on startup

**Solutions:**

1. **Check ObjectiveManager initialization:**
   - Look for "Initialized X sample objectives" in logs
   - Review console for initialization errors

2. **Manually add an objective:**
   - Click '+' button
   - Try adding shop objective manually
   - If this works, sample data initialization issue

---

## Performance Issues

### Problem: UI is slow or freezing

**Symptoms:**
- Clicking takes time to respond
- Debug panel refresh is slow
- Window feels laggy

**Solutions:**

1. **Disable auto-refresh:**
   - Uncheck "Auto-refresh (1s)" checkbox in debug panel
   - Refresh manually when needed

2. **Reduce number of objectives:**
   - Clear all objectives
   - Add fewer objectives for testing

3. **Check system resources:**
   - Monitor CPU and memory usage
   - Close other applications

### Problem: Debug panel takes long to refresh

**Symptoms:**
- Noticeable delay when clicking "Refresh"
- Auto-refresh causes stuttering

**Solutions:**

1. **Increase refresh interval:**
   ```java
   private static final int AUTO_REFRESH_INTERVAL_MS = 2000; // Increase this
   ```

2. **Disable auto-refresh:**
   - Use manual refresh (F5) instead

---

## Export/Copy Features Not Working

### Problem: "Copy" button doesn't copy to clipboard

**Symptoms:**
- Click copy but clipboard unchanged
- Error message when clicking copy

**Solutions:**

1. **Check clipboard permissions:**
   - Some systems restrict clipboard access
   - Try running with different permissions

2. **Verify AWT headless mode:**
   - Ensure not running in headless mode
   - Check `-Djava.awt.headless=false`

3. **Use export instead:**
   - Export to file as workaround
   - Then open file and copy manually

### Problem: Export file dialog doesn't appear

**Symptoms:**
- Click export but nothing happens
- No file chooser dialog

**Solutions:**

1. **Check for dialog behind window:**
   - Alt+Tab to check all windows
   - Dialog may be behind main window

2. **Try keyboard shortcut:**
   - Dialog should appear in foreground

---

## Keyboard Shortcuts Not Working

### Problem: F5, Ctrl+R, or Ctrl+Q don't work

**Symptoms:**
- Pressing shortcuts has no effect
- Only mouse clicks work

**Solutions:**

1. **Ensure window has focus:**
   - Click on the window
   - Make sure it's the active window

2. **Check for key binding conflicts:**
   - Some OSs/apps override these keys
   - Try clicking buttons instead

3. **Verify input map registration:**
   - Look for "Keyboard shortcuts registered" in logs
   - If missing, shortcuts weren't set up

---

## Getting Help

### Collecting Diagnostic Information

When reporting issues, include:

1. **Java Version:**
   ```bash
   java -version
   ```

2. **Operating System:**
   - Windows 10/11, macOS version, Linux distribution

3. **Console Output:**
   - Copy all console output
   - Include error stack traces

4. **Steps to Reproduce:**
   - Exact steps that cause the issue
   - What you expected vs. what happened

5. **Screenshots:**
   - UI state when error occurs
   - Error dialogs

### Debug Logging

Enable verbose logging:

```java
// Add to TestHarnessRunner before creating UI
System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
```

### Common Log Messages

**Normal Operation:**
```
INFO ObjectiveDebugPanel initialized
INFO Loaded 15 shops from database
INFO Initialized 3 sample objectives
INFO Auto-refresh timer started (interval: 2000ms)
INFO Keyboard shortcuts registered: F5, Ctrl+R, Ctrl+Q
```

**Errors to Watch For:**
```
ERROR Error initializing Test Harness UI
ERROR Error refreshing debug view
ERROR Error copying to clipboard
ERROR Error exporting to file
```

### Quick Diagnostic Checklist

- [ ] Java 11+ installed
- [ ] Project compiles without errors
- [ ] shops.json file exists and is valid
- [ ] Lombok annotation processing enabled
- [ ] No firewall blocking Gradle
- [ ] Display scaling at 100% or test at different scales
- [ ] Sufficient disk space for exports
- [ ] No other Java processes interfering

---

## Advanced Troubleshooting

### Clearing IDE Caches

**IntelliJ IDEA:**
```
File → Invalidate Caches / Restart → Invalidate and Restart
```

**Eclipse:**
```
Project → Clean → Clean all projects
```

### Rebuilding from Scratch

```bash
./gradlew clean
rm -rf build/
rm -rf .gradle/
./gradlew build
./gradlew runTestHarness
```

### Running with Debug Output

```bash
./gradlew runTestHarness --debug --stacktrace
```

### Checking File Permissions

```bash
# Make sure source files are readable
find src/ -type f -exec chmod 644 {} \;
find src/ -type d -exec chmod 755 {} \;
```

---

## Still Having Issues?

If none of these solutions work:

1. **Check the README files:**
   - `TEST_HARNESS_GUIDE.md` - Usage guide
   - `src/test/java/com/questnextaction/testharness/README.md` - Component details

2. **Review the code:**
   - All classes have comprehensive JavaDoc
   - Read comments for implementation details

3. **Run unit tests:**
   ```bash
   ./gradlew test
   ```

4. **Create a minimal test case:**
   - Start with empty objectives
   - Add one objective at a time
   - Identify what triggers the issue

5. **Report the issue:**
   - Include diagnostic information
   - Provide steps to reproduce
   - Include your environment details
