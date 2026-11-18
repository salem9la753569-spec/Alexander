# Alexander GitHub Actions fix

This archive adds the missing `.github/workflows/android-build.yml` workflow.

## Install it
1. Extract this ZIP.
2. In your Alexander repository, upload the `.github` folder to the repository root.
3. Make sure the repository also contains the complete Android Gradle project:
   - `gradlew`
   - `gradle/wrapper/`
   - `settings.gradle` or `settings.gradle.kts`
   - the Android app module (usually `app/`)
4. Commit the files to `main` or `master`.

## Run and download
- A push to `main` or `master` starts the build automatically.
- You can also open **Actions → Build Alexander APK → Run workflow**.
- When it succeeds, open the run and download **Alexander-APK** under Artifacts.

This workflow builds a debug APK. It does not add missing app source code or Gradle files.
If the build fails, inspect the first red error in the Actions log.
