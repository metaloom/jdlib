# Jdlib

Java Wrapper For dlib for Linux and MacOSX. Till now the wrapper contains stubs for face HOG detector, facial landmarks, face embedding models.

# Fork Note

This is a fork of [jdlib](https://github.com/tahaemara/jdlib) which was initially created by Taha Emara.

This fork supports a way to run the CNN based face detection which can utilize the GPU.

## Demo
### Face Clustering Example

<img src="https://media.giphy.com/media/FD9AfNUw3VX8CqYaph/giphy.gif" width="700" height="400" />

## Using Jdlib

```xml
<dependency>
  <groupId>io.metaloom.jdlib</groupId>
  <artifactId>jdlib</artifactId>
  <version>${project.version}</version>
</dependency>
```

## Compiling Jdlib

### Requirements:

- Dlib installation requirements [Using dlib from C++](http://dlib.net/compile.html)
- JDK 17 or newer
- Maven
- GCC 13

```
Debian 11:

* nvidia-cuda-toolkit
* nvidia-cudnn 

```

### Clone project:

```bash
git clone https://github.com/metaloom/jdlib.git
```

### Compile JNI/C++ code:

```bash
# Generate needed JNI header files via mvn
mvn clean compile

# Now build JNI lib
mkdir -p build && cd build

# Ensure to select a compatible compiler (Example for Debian 11)
export JAVA_HOME=/opt/jvm/java17
CC=gcc-13 CXX=/usr/bin/g++-13 cmake ../jni
make 
```

### Compile Java Package:

```bash
mvn clean package
```

After that you will have the JAR file including the binaries for your platform inside Jdlib/target. Then you can use it inside your project as an external jar or install it manually in [local maven](https://maven.apache.org/guides/mini/guide-3rd-party-jars-local.html). 

## Compiling and running examples

- Download needed models to example folder

```bash
cd examples
wget http://dlib.net/files/shape_predictor_68_face_landmarks.dat.bz2
wget http://dlib.net/files/dlib_face_recognition_resnet_model_v1.dat.bz2
wget http://dlib.net/files/mmod_human_face_detector.dat.bz2

bzip2 -dk shape_predictor_68_face_landmarks.dat.bz2
bzip2 -dk dlib_face_recognition_resnet_model_v1.dat.bz2
bzip2 -dk mmod_human_face_detector.dat.bz2
```

- Build and run examples

```bash
cd examples
mvn clean package
java -jar target/clustering-example-jar-with-dependencies.jar
java -jar target/landmarks-example-jar-with-dependencies.jar
java -jar target/cnn-facedetect-example-jar-with-dependencies.jar
```

## Releasing

```bash
# Set release version and commit changes
mvn versions:set -DgenerateBackupPoms=false
git add pom.xml ; git commit -m "Prepare release"

# Invoke release
mvn clean deploy -Drelease
```