package com.wuyumoom.yuqqbot.api.config

/**
 * Thrown when loading a [Configuration] from text or a file fails.
 */
class InvalidConfigurationException : Exception {
    constructor() : super()
    constructor(msg: String?) : super(msg)
    constructor(cause: Throwable?) : super(cause)
    constructor(msg: String?, cause: Throwable?) : super(msg, cause)

    companion object {
        private const val serialVersionUID = 1L
    }
}
