# Native FFI contract

The host-specific native bridge exports a small C ABI described by [tinycc.h](../src/main/native/include/tinycc.h). Its stable entry point is:

    int tinycc_main(int argc, char *argv[]);

The function follows the parameter and return convention of a C main function, but is an ordinary exported function named tinycc_main. On Windows it is not DllMain: Windows calls DllMain during library load/unload, with a different signature and loader-lock restrictions. The FFI caller explicitly invokes tinycc_main after loading the library.

## Call behavior

- argc is the number of entries in argv. argv[0] is the caller's program name; entries from argv[1] onward become CLI arguments.
- C strings are UTF-8 on every supported host. Pass argc >= 1 and a non-null argv array whose elements are non-null.
- The bridge starts a child Java process with the bundled JAR and the argument vector. It does not attach to, create, or destroy a JVM inside the caller's process.
- The bridge inherits the caller's standard input, output, and error streams. tinycc_main returns the CLI exit code and never exits the caller process.
- The bridge does not retain argv pointers or source/output paths. Independent calls can run concurrently; child output may interleave if callers run simultaneously.
- tinycc_set_jar_path copies its argument and changes the JAR path used by later calls. It returns 0 on success. Configure this process-wide value before concurrent calls; NULL clears the override.
- tinycc_last_error returns a thread-local bridge error string. It describes Java/JAR discovery or process launch failures; compiler diagnostics are written by the CLI.

## Runtime and JAR discovery

The bridge requires a Java 17 or newer launcher. Select it in this order:

1. TINYCC_JAVA environment variable.
2. JAVA_HOME/bin/java (java.exe on Windows).
3. java found on PATH.

Select the JAR in this order:

1. Path previously set with tinycc_set_jar_path.
2. TINYCC_JAR environment variable.
3. tinycc.jar beside the loaded bridge library.

Java process creation errors set tinycc_last_error. Exit value 127 means no Java launcher was found; 126 means Java or the requested JAR could not be launched. Otherwise, the return value is the tinycc CLI's exit status.

The shared library must match the caller process ABI. Linux ships separate glibc and musl libraries for each host architecture. Kotlin/Java bindings can auto-select from /proc/self/maps and accept the tinycc.native.libc system-property override. Direct native callers choose the matching library variant.

## Caller examples

Python ctypes callers load the correct host library and set the exported function signature:

    import ctypes

    bridge = ctypes.CDLL("libtinycc.so")
    bridge.tinycc_main.argtypes = [ctypes.c_int, ctypes.POINTER(ctypes.c_char_p)]
    bridge.tinycc_main.restype = ctypes.c_int

    args = [b"tinycc", b"--list-targets"]
    argv = (ctypes.c_char_p * len(args))(*args)
    result = bridge.tinycc_main(len(args), argv)

Java and Kotlin applications should normally use the in-process Kotlin API in org.tinycc.api. The C ABI remains available to callers that already use a native binding such as JNA.
