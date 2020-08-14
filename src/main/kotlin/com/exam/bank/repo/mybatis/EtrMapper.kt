package com.exam.bank.repo.mybatis

import com.exam.bank.entity.ComEtrMst
import org.apache.ibatis.annotations.Param
import org.springframework.stereotype.Repository

@Repository
interface EtrMapper {

    fun updateListEtrCancel(@Param("trId") trId: String , @Param("userId") userId: Int )
    fun updateOneEtrCancelBySeq(@Param("seq") seq: Int)
    fun updateListEtrCancelBySeqList(@Param("listSeq") listSeq: List<Int>)
    fun updateEtrTimeout(@Param("seq") seq: Int)
    fun updateEtrErr(@Param("seq") seq: Int)
    fun updateEtrSending(@Param("seq") seq: Int)
    fun updateEtrSuccess(@Param("seq") seq: Int)
    fun insertEtr(input: ComEtrMst) : Int // seq 리턴 ( insert 건수 아님 )
    fun selectListReqTr(@Param("trId") trId: String) : MutableCollection<ComEtrMst>
    fun selectOneForValid(@Param("trId") trId: String, @Param("userId") userId: Int): Boolean
    fun selectOneIsOnlyValidMe(@Param("seq") seq:Int, @Param("trId") trId: String, @Param("userId") userId: Int): Boolean

}
