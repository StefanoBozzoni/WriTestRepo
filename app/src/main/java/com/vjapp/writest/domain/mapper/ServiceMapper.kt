package com.vjapp.writest.domain.mapper

import com.vjapp.writest.data.remote.model.ClassResponseObj
import com.vjapp.writest.data.remote.model.SchoolResponseObj
import com.vjapp.writest.data.remote.model.UploadFilesRequest
import com.vjapp.writest.data.remote.model.UploadFilesResponse
import com.vjapp.writest.domain.model.ClassObjEntity
import com.vjapp.writest.domain.model.SchoolObjEntity
import com.vjapp.writest.domain.model.UploadFilesRequestEntity
import com.vjapp.writest.domain.model.UploadFilesResponseEntity

class ServiceMapper {

    companion object {
        fun mapToEntity(uploadFilesResponse: UploadFilesResponse): UploadFilesResponseEntity {
            return UploadFilesResponseEntity(
                uploadFilesResponse.esito
            )
        }

        fun mapToModel(uploadFilesRequestEntity: UploadFilesRequestEntity): UploadFilesRequest {
            with (uploadFilesRequestEntity) {
                return UploadFilesRequest(
                    fileImgPath = fileImgPath,
                    fileVideoPath = fileVideoPath,
                    token = token
                )
            }
        }
        fun mapToEntity(classResponseObj: ClassResponseObj): ClassObjEntity {
            return ClassObjEntity(
                type = classResponseObj.type
            )
        }

        fun mapToEntity(schoolResponseObj: SchoolResponseObj): SchoolObjEntity {
            return SchoolObjEntity(
                codSchool = schoolResponseObj.codSchool,
                nameSchool = schoolResponseObj.nameSchool
            )
        }

    }

}