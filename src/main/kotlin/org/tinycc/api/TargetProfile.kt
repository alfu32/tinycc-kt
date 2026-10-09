package org.tinycc.api

/** A target profile supported by the packaged compiler distribution. */
enum class TargetProfile(
    val id: String,
    val llvmTriple: String,
    val requiresAppleSdk: Boolean = false
) {
    LINUX_X86_64_MUSL("linux_x86_64_musl", "x86_64-unknown-linux-musl"),
    LINUX_AARCH64_MUSL("linux_aarch64_musl", "aarch64-unknown-linux-musl"),
    LINUX_RISCV64_MUSL("linux_riscv64_musl", "riscv64-unknown-linux-musl"),
    WINDOWS_X86_64_MINGW_UCRT("windows_x86_64_mingw_ucrt", "x86_64-w64-windows-gnu"),
    WINDOWS_AARCH64_MINGW_UCRT("windows_aarch64_mingw_ucrt", "aarch64-w64-windows-gnu"),
    MACOS_AARCH64_DARWIN("macos_aarch64_darwin", "arm64-apple-macosx", requiresAppleSdk = true);

    companion object {
        @JvmStatic
        fun fromId(id: String): TargetProfile? = entries.firstOrNull { it.id == id }
    }
}
