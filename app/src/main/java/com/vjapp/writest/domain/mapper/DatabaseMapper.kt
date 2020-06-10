package com.vjapp.writest.domain.mapper

import com.vjapp.writest.data.local.model.CachedTest
import com.vjapp.writest.data.remote.model.UploadFilesRequest
import com.vjapp.writest.domain.model.TestEntity
import com.vjapp.writest.domain.model.UploadFilesRequestEntity

class DatabaseMapper {

    companion object {
        fun mapToEntity(cachedTest: CachedTest): TestEntity {
            with (cachedTest) {
                return TestEntity(
                    sendDate    = sendDate,
                    token       = token,
                    videoUri    = videoUri,
                    imgUri      = imgUri,
                    school      = school,
                    classType   = classType,
                    iDSchool    = iDSchool,
                    iDClassType = iDClassType,
                    idTest      = idTest
                )
            }
        }

        fun mapToModel(testEntity: TestEntity): CachedTest {
            with (testEntity) {
                return CachedTest(
                    sendDate    = sendDate,
                    token       = token,
                    videoUri    = videoUri,
                    imgUri      = imgUri,
                    school      = school,
                    classType   = classType,
                    iDSchool    = iDSchool,
                    iDClassType = iDClassType,
                    idTest      = idTest
                )
                //x.idTest=testEntity.idTest
            }
        }
    }

}