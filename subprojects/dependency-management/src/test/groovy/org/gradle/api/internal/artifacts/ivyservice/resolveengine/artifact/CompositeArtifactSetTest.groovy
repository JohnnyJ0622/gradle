/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.api.internal.artifacts.ivyservice.resolveengine.artifact

import org.gradle.internal.operations.BuildOperationQueue
import spock.lang.Specification

class CompositeArtifactSetTest extends Specification {
    def "factory method returns empty when no non-empty sets provided"() {
        expect:
        CompositeArtifactSet.of([]) == ResolvedArtifactSet.EMPTY
        CompositeArtifactSet.of([ResolvedArtifactSet.EMPTY, ResolvedArtifactSet.EMPTY]) == ResolvedArtifactSet.EMPTY
    }

    def "factory method returns single set when single non-empty sets provided"() {
        def set = Mock(ResolvedArtifactSet)

        expect:
        CompositeArtifactSet.of([set]) == set
        CompositeArtifactSet.of([ResolvedArtifactSet.EMPTY, set, ResolvedArtifactSet.EMPTY]) == set
    }

    def "visits each set in turn"() {
        def set1 = Mock(ResolvedArtifactSet)
        def set2 = Mock(ResolvedArtifactSet)

        def asyncListener = Mock(ResolvedArtifactSet.AsyncArtifactListener)
        def queue = Stub(BuildOperationQueue)
        def visitor = Mock(ArtifactVisitor)
        def result1 = Mock(ResolvedArtifactSet.Completion)
        def result2 = Mock(ResolvedArtifactSet.Completion)

        when:
        def result = CompositeArtifactSet.of([set1, set2]).startVisit(queue, asyncListener)

        then:
        1 * set1.startVisit(queue, asyncListener) >> result1
        1 * set2.startVisit(queue, asyncListener) >> result2
        0 * _

        when:
        result.visit(visitor)

        then:
        1 * result1.visit(visitor)

        then:
        1 * result2.visit(visitor)
        0 * _
    }
}
