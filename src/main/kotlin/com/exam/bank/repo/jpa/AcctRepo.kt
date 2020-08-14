package com.exam.bank.repo.jpa

import com.exam.bank.entity.ActStgMst
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AcctRepo : CrudRepository<ActStgMst, Int>


