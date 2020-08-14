package com.exam.bank.service

import com.exam.bank.entity.ComPushMst
import com.exam.bank.repo.jpa.PushRepo
import com.exam.fwk.core.base.BaseService
import com.exam.fwk.custom.util.DateUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * 사용자 푸쉬 서비스
 * - 사용자에게 보여지는게 아닌, 모바일 디바이스의 화면 이동 시그널
 */
@Service
class PushService : BaseService() {

    @Autowired lateinit var repo: PushRepo

    /**
     * PUSH_ID
     * P1: 타행 계좌의 명의 정보를 얻어오는데 실패 하였습니다. 잠시 후 재시도 해주세요.
     * P2: 타행 계좌 명의 확인 완료. 다음단계 진행.
     * P3: 명의가 일치하지 않습니다. 다른 계좌로 진행 해 주세요.
     * P4: 계좌 이체 적요란의 단어를 입력하시어요.
     */
    enum class PUSH_ID {
        P1, P2, P3, P4
    }

    /**
     * Push 등록
     * - End Device 에 푸쉬
     * - Push Log Table 에만 insert
     * - 실제 푸쉬는 미구현
     */
    fun regPush(pushId: PUSH_ID, userId: Int) {
        // push log insert
        val inSave = ComPushMst()
        inSave.pushId = pushId.toString()
        inSave.userId = userId

        inSave.createDt = DateUtils.currentTimeStamp()
        inSave.createUserId = area.commons.user?.userId

        repo.save(inSave)
    }

}
