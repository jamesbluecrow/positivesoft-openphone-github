package club.positivesoft.openphone.github.domain.exceptions

class NoInternetConnectionException(private val throwable: Throwable? = null) : Exception(throwable)