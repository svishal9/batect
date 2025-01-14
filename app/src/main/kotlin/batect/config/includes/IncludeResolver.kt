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

package batect.config.includes

import batect.config.FileInclude
import batect.config.GitInclude
import batect.config.Include
import java.nio.file.Path
import java.util.concurrent.ConcurrentHashMap

class IncludeResolver(private val gitRepositoryCache: GitRepositoryCache) {
    private val gitRepositoryPaths = ConcurrentHashMap<GitRepositoryReference, Path>()

    fun resolve(include: Include, listener: GitRepositoryCacheNotificationListener): Path = when (include) {
        is FileInclude -> include.path
        is GitInclude -> {
            val rootPath = rootPathFor(include.repositoryReference, listener)

            rootPath.resolve(include.path)
        }
    }

    fun rootPathFor(repo: GitRepositoryReference, listener: GitRepositoryCacheNotificationListener): Path = gitRepositoryPaths.getOrPut(repo) {
        gitRepositoryCache.ensureCached(repo, listener)
    }
}
