package it.laface.base

sealed interface NetworkResult<out T> {
    data class Success<out T>(val value: T) : NetworkResult<T>
    data class Failure(val error: NetworkError) : NetworkResult<Nothing>
}
