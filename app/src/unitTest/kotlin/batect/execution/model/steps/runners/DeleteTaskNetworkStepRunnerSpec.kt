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

package batect.execution.model.steps.runners

import batect.docker.DockerNetwork
import batect.docker.api.NetworkDeletionFailedException
import batect.docker.client.NetworksClient
import batect.execution.model.events.TaskEventSink
import batect.execution.model.events.TaskNetworkDeletedEvent
import batect.execution.model.events.TaskNetworkDeletionFailedEvent
import batect.execution.model.steps.DeleteTaskNetworkStep
import batect.testutils.createForEachTest
import batect.testutils.on
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object DeleteTaskNetworkStepRunnerSpec : Spek({
    describe("running a 'delete task network' step") {
        val network = DockerNetwork("some-network")
        val step = DeleteTaskNetworkStep(network)

        val networksClient by createForEachTest { mock<NetworksClient>() }
        val eventSink by createForEachTest { mock<TaskEventSink>() }

        val runner by createForEachTest { DeleteTaskNetworkStepRunner(networksClient) }

        on("when deleting the network succeeds") {
            beforeEachTest { runner.run(step, eventSink) }

            it("deletes the network") {
                verify(networksClient).delete(network)
            }

            it("emits a 'network deleted' event") {
                verify(eventSink).postEvent(TaskNetworkDeletedEvent)
            }
        }

        on("when removing the container fails") {
            beforeEachTest {
                whenever(networksClient.delete(network)).thenThrow(NetworkDeletionFailedException("some-network", "Something went wrong"))

                runner.run(step, eventSink)
            }

            it("emits a 'network deletion failed' event") {
                verify(eventSink).postEvent(TaskNetworkDeletionFailedEvent("Something went wrong"))
            }
        }
    }
})
