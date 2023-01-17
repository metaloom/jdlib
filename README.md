# Jdlib

Java Wrapper For dlib for Linux and MacOSX. Till now the wrapper contains stubs for face HOG detector, facial landmarks, face embedding models.

# Fork Note

This is a fork of [jdlib](https://github.com/tahaemara/jdlib) which was initially created by Taha Emara.

## Demo
### Face Clustering Example

<img src="https://media.giphy.com/media/FD9AfNUw3VX8CqYaph/giphy.gif" width="700" height="400" />

## Using Jdlib

JAR files contains binaries of Jdlib on current supprted platforms (Linux and Mac OS X) inside [releases](https://github.com/metaloom/jdlib/releases).

Also you can have everything downloaded and installed automatically with Maven after including the next dependency inside the pom.xml.

```
<dependency>
  <groupId>io.metaloom.jdlib</groupId>
  <artifactId>jdlib</artifactId>
  <version>2.0.0</version>
</dependency>
```

## Compiling Jdlib

### Requirements:

- Dlib installation requirements [Using dlib from C++](http://dlib.net/compile.html)
- JDK 11 or newer
- Maven

```
Debian 11:

* nvidia-cuda-toolkit
* nvidia-cudnn 

```

### Clone project:

```
git clone https://github.com/metaloom/jdlib.git
```


### Compile JNI/C++ code:

```bash
# Generate needed JNI header files via mvn
mvn compile

# Now build JNI lib
mkdir build && cd build

# Ensure to select a compatible compiler (Example for Debian 11)
CC=gcc-10 CXX=/usr/bin/g++-10 cmake ../jni
make 
```

### Compile Java Package:

```
mvn clean package
```

After that you will have the JAR file including the binaries for your platform inside Jdlib/target. Then you can use it inside your project as an external jar or install it manually in [local maven](https://maven.apache.org/guides/mini/guide-3rd-party-jars-local.html). 

## Compiling and running examples

- Download needed models to example folder

```bash
cd examples
wget http://dlib.net/files/shape_predictor_68_face_landmarks.dat.bz2
wget http://dlib.net/files/dlib_face_recognition_resnet_model_v1.dat.bz2
```
- Unzip model

```
bzip2 -dk shape_predictor_68_face_landmarks.dat.bz2
bzip2 -dk dlib_face_recognition_resnet_model_v1.dat.bz2
```

- Build and run examples

```
cd examples
mvn clean package
java -jar target/clustering-example-jar-with-dependencies.jar
java -jar target/landmarks-example-jar-with-dependencies.jar
```
