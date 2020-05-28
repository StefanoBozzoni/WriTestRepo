package com.vjapp.writest.data.mapper

import com.vjapp.writest.data.model.UploadFilesRequest
import com.vjapp.writest.data.model.UploadFilesResponse
import com.vjapp.writest.domain.model.UploadFilesRequestEntity
import com.vjapp.writest.domain.model.UploadFilesResponseEntity

class UploadFilesMapper {

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
    }

}