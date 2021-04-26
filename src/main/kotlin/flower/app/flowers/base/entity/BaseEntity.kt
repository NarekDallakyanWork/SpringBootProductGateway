package flower.app.flowers.base.entity

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.io.Serializable
import java.util.*
import javax.persistence.*

@MappedSuperclass
open class BaseEntity : Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, updatable = false)
    var id: Long = -1

    @CreationTimestamp
    val createdDate: Date? = null

    @UpdateTimestamp
    val lastModifiedDate: Date? = null
}
