package com.ask.set.entity

import org.hibernate.Hibernate
import org.hibernate.annotations.GenericGenerator
import javax.persistence.*

@Entity
@Table(name = "tb_company")
class Company(

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "company_id")
    var id: String? = null,

    @Column(nullable = false, length = 30)
    var name: String,

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "company")
    @OrderBy("priority ASC")
    val users: Set<User> = hashSetOf()

) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Company

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "Company(id=$id, name='$name')"
    }

}
