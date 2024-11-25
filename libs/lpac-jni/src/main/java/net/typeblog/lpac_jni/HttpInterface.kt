package net.typeblog.lpac_jni

import javax.net.ssl.TrustManager

/*
 * Should reflect euicc_http_interface in lpac/euicc/interface.h
 */
interface HttpInterface {
    data class HttpResponse(val rcode: Int, val data: ByteArray) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as HttpResponse

            if (rcode != other.rcode) return false
            if (!data.contentEquals(other.data)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = rcode
            result = 31 * result + data.contentHashCode()
            return result
        }
    }

    /**
     * The last HTTP response we have received from the SM-DP+ server.
     *
     * This is intended for error diagnosis. However, note that most SM-DP+ servers
     * respond with 200 even when there is an error. This needs to be taken into
     * account when designing UI.
     */
    val lastHttpResponse: HttpResponse?

    /**
     * The last exception that has been thrown during a HTTP connection
     */
    val lastHttpException: Exception?

    fun transmit(url: String, tx: ByteArray, headers: Array<String>): HttpResponse
    // The LPA is supposed to pass in a list of pkIds supported by the eUICC.
    // HttpInterface is responsible for providing TrustManager implementations that
    // validate based on certificates corresponding to these pkIds
    fun usePublicKeyIds(pkids: Array<String>)
}