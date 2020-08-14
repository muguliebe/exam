package com.exam.bank.repo.jpa

import com.exam.bank.entity.ComEtrMst
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface EtrRepo : CrudRepository<ComEtrMst, Int> {
    fun findByTrIdAndTrStatCd(trId:String, trStatCd: String): MutableCollection<ComEtrMst>
}

