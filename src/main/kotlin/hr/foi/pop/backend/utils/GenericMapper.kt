package hr.foi.pop.backend.utils

interface GenericMapper<D, E> {
    fun mapDto(e : E) : D
    fun map(d : D) : E
}