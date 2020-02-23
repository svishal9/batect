/*
   Copyright 2017-2020 Charles Korn.

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

package batect.execution

import batect.config.ExpressionEvaluationContext
import batect.config.LiteralValue
import batect.config.ExpressionEvaluationException
import batect.config.LocalMount
import batect.config.VolumeMount
import batect.docker.DockerVolumeMount
import batect.os.PathResolutionResult
import batect.os.PathResolver
import batect.utils.mapToSet

class VolumeMountResolver(
    private val pathResolver: PathResolver,
    private val expressionEvaluationContext: ExpressionEvaluationContext
) {
    fun resolve(mounts: Set<VolumeMount>): Set<DockerVolumeMount> = mounts.mapToSet {
        when (it) {
            is LocalMount -> resolve(it)
        }
    }

    private fun resolve(mount: LocalMount): DockerVolumeMount {
        val evaluatedLocalPath = evaluateLocalPath(mount)

        return when (val resolvedLocalPath = pathResolver.resolve(evaluatedLocalPath)) {
            is PathResolutionResult.Resolved -> DockerVolumeMount(resolvedLocalPath.absolutePath.toString(), mount.containerPath, mount.options)
            else -> {
                val expressionDisplay = if (mount.localPath is LiteralValue) {
                    "'${mount.localPath.value}'"
                } else {
                    "expression '${mount.localPath.originalExpression}' (evaluated as '$evaluatedLocalPath')"
                }

                throw VolumeMountResolutionException("Could not resolve volume mount path: $expressionDisplay is not a valid path.")
            }
        }
    }

    private fun evaluateLocalPath(mount: LocalMount): String {
        try {
            return mount.localPath.evaluate(expressionEvaluationContext)
        } catch (e: ExpressionEvaluationException) {
            throw VolumeMountResolutionException("Could not resolve volume mount path: expression '${mount.localPath.originalExpression}' could not be evaluated: ${e.message}", e)
        }
    }
}

class VolumeMountResolutionException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)
