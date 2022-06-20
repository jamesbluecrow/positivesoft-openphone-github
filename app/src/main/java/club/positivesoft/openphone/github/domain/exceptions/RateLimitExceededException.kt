package club.positivesoft.openphone.github.domain.exceptions

class RateLimitExceededException(private val throwable: Throwable? = null) : Exception(throwable)