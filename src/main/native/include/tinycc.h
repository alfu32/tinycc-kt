#ifndef TINYCC_H_INCLUDED
#define TINYCC_H_INCLUDED

#ifdef _WIN32
#  ifdef TINYCC_BUILD
#    define TINYCC_API __declspec(dllexport)
#  else
#    define TINYCC_API __declspec(dllimport)
#  endif
#  define TINYCC_CALL __cdecl
#else
#  define TINYCC_API __attribute__((visibility("default")))
#  define TINYCC_CALL
#endif

#ifdef __cplusplus
extern "C" {
#endif

#define TINYCC_ABI_VERSION 1
#define TINYCC_BRIDGE_LAUNCH_ERROR 126
#define TINYCC_JAVA_NOT_FOUND 127

/* Returns the integer version of the exported C ABI. */
TINYCC_API int TINYCC_CALL tinycc_abi_version(void);

/*
 * Sets the JAR used by subsequent tinycc_main calls.
 * The bridge copies this UTF-8 path. Pass NULL to clear the explicit path.
 * Returns 0 on success and a nonzero value when the path is invalid.
 */
TINYCC_API int TINYCC_CALL tinycc_set_jar_path(const char *utf8_jar_path);

/*
 * Runs the tinycc CLI in a child JVM and returns its exit status.
 * argv[0] is the caller's program name and is not forwarded to the CLI.
 * The caller owns argv and all strings; the bridge does not retain them.
 */
TINYCC_API int TINYCC_CALL tinycc_main(int argc, char *argv[]);

/*
 * Returns the most recent bridge error for the calling thread, or an empty
 * string if the last call did not fail while locating or launching Java/JAR.
 * The returned pointer is owned by the bridge and is valid until its next
 * bridge call on the same thread.
 */
TINYCC_API const char *TINYCC_CALL tinycc_last_error(void);

#ifdef __cplusplus
}
#endif

#endif
