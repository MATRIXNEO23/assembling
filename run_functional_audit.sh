#!/usr/bin/env bash
set -euo pipefail

if [ -d "/c/Program Files/Eclipse Adoptium/jdk-17.0.20.101-hotspot" ]; then
  export JAVA_HOME="/c/Program Files/Eclipse Adoptium/jdk-17.0.20.101-hotspot"
  export PATH="$JAVA_HOME/bin:$PATH"
fi

if command -v gradle >/dev/null 2>&1; then
  :
elif [ -x "$HOME/gradle/gradle-9.7.1/bin/gradle" ]; then
  export PATH="$HOME/gradle/gradle-9.7.1/bin:$PATH"
elif [ -x "/c/Users/matri/gradle/gradle-9.7.1/bin/gradle" ]; then
  export PATH="/c/Users/matri/gradle/gradle-9.7.1/bin:$PATH"
else
  echo "ERROR: Gradle 9.7.1 non trovato."
  echo "Cercato in PATH e in ~/gradle/gradle-9.7.1/bin."
  exit 127
fi

echo "JAVA: $(java -version 2>&1 | head -n 1)"
echo "GRADLE: $(gradle --version | awk '/Gradle / {print $2; exit}')"
echo

exec bash verify_understanding_authority_functional.sh
