package com.ask.set

import com.ask.set.entity.Company
import com.ask.set.entity.User
import org.assertj.core.api.Assertions.assertThat
import org.hibernate.collection.internal.PersistentSet
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
internal class CompanyTest(
    @Autowired private val testEntityManager: TestEntityManager
) {

    @DisplayName("HashSet 과 @OrderBy 사용시 순서 검증")
    @Test
    internal fun verifySetOrder() {
        // given
        val companyId = "company01"

        // when
        val company = testEntityManager.find(Company::class.java, companyId)
        val users = company.users

        // then
        assertThat(users).isInstanceOf(PersistentSet::class.java)
        assertThat(users).flatMap(User::priority).containsSequence(1L, 2L, 3L)
    }

}
