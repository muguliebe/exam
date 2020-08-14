package com.exam.bank.repo.jpa

import com.exam.bank.entity.ComPushMst
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PushRepo : CrudRepository<ComPushMst, Int>

