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

package batect.testutils

import batect.config.Container
import batect.docker.DockerContainer
import batect.docker.DockerNetwork
import batect.execution.model.steps.DeleteTaskNetworkStep
import batect.execution.model.steps.RunContainerStep
import batect.execution.model.steps.TaskStep
import batect.utils.generateId

fun createMockTaskStep(countsAgainstParallelismCap: Boolean = true): TaskStep =
    if (countsAgainstParallelismCap) {
        DeleteTaskNetworkStep(DockerNetwork(generateId(10)))
    } else {
        RunContainerStep(Container(generateId(10), imageSourceDoesNotMatter()), DockerContainer(generateId(10)))
    }
