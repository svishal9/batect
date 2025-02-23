/*
    Copyright 2017-2021 Charles Korn.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

package batect.sockets.unix

import batect.testutils.equalTo
import batect.testutils.on
import batect.testutils.withMessage
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.containsSubstring
import com.natpryce.hamkrest.throws
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.net.InetAddress

object UnixSocketDnsSpec : Spek({
    describe("a Unix socket DNS provider") {
        val dns = UnixSocketDns()

        describe("looking up a host name") {
            on("when that host name is the output from encodePath()") {
                val hostName = UnixSocketDns.encodePath("/var/run/docker.sock")

                it("returns a dummy resolved address") {
                    assertThat(
                        dns.lookup(hostName),
                        equalTo(
                            listOf(
                                InetAddress.getByAddress(hostName, byteArrayOf(0, 0, 0, 0))
                            )
                        )
                    )
                }
            }

            on("when that host name has not been encoded with encodePath()") {
                it("throws an appropriate exception") {
                    assertThat({ dns.lookup("/var/run/docker.sock") }, throws<IllegalArgumentException>(withMessage("Host name '/var/run/docker.sock' was not encoded for use with UnixSocketDns.")))
                }
            }
        }

        describe("encoding and decoding paths") {
            on("encoding a path") {
                val encoded = UnixSocketDns.encodePath("/var/run/thing.sock")

                it("returns a value that does not contain any slashes") {
                    assertThat(encoded, !containsSubstring("/"))
                }

                it("returns a value that can be recovered by passing it to decodePath()") {
                    assertThat(UnixSocketDns.decodePath(encoded), equalTo("/var/run/thing.sock"))
                }
            }

            on("decoding a path that was not encoded with encodePath()") {
                it("throws an appropriate exception") {
                    assertThat({ UnixSocketDns.decodePath("www.example.com") }, throws<IllegalArgumentException>(withMessage("Host name 'www.example.com' was not encoded for use with UnixSocketDns.")))
                }
            }
        }
    }
})
