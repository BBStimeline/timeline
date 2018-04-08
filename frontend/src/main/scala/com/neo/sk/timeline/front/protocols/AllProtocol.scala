package com.neo.sk.timeline.front.protocols

/**
  * Created by zx0 on 2018/3/21.
  */
object AllProtocol {

  case class BaseUserInfo(
                           userType: String,
                           userId: String,
                           name: String,
                           shortName: String,
                           mobile: String,    //仅为用户时存在
                           sex: Short       //仅为用户时存在
                         )

  case class BaseUserInfoRsp(
                              data: Option[BaseUserInfo],
                              errCode: Int = 0,
                              msg: String = "ok"
                            )

  case class UploadFileRsp(
                            id: Option[Long],
                            errCode: Int = 0,
                            msg: String = "ok"
                          )

  case class UploadFileAgainRsp(
                            data: Option[Attach],
                            errCode: Int = 0,
                            msg: String = "ok"
                          )



  case class AddVersionRsp(
                            id: Option[Long],
                            errCode: Int = 0,
                            msg: String = "ok"
                          )

  case class BaseVersionInfoRsp(
                                 data: Option[BaseVersionInfo],
                                 errCode: Int = 0,
                                 msg: String = "ok"
                               )

  case class BaseVersionInfo(
                              id: Long,
                              versionId: String,
                              description: String,
                              time: Long,
                              author: String,
                              state: Int,
                              attach: List[Attach],
                              history: List[Version]
                            )

  case class Version(
                      id: Long,
                      versionId: String,
                      state:Int
                    )

  case class Attach(
                     id: Long,
                     fileName: String,
                     url: String,
                     order: Int
                   )

  case class UpdateVersionRep(
                               id: Long,
                               versionId: String,
                               resourceId: Long,
                               abilityId: Long,
                               description: String,
                               author: String,
                               attach: List[Long]
                             )

  case class DeleteVersionRep(
                        id: Long,
                        resourceId: Long
                      )

  case class RecoverDeleteVersionRep(
                               id: Long
                             )

  case class DeleteFileRep(
                               id: Long
                             )

  case class ChangerOrderRep(
                              versionId: Long,
                              originId: Long,
                              originOrder: Int,
                              changeType: Int //0為往上升，1為往下降
                            )


  //AbilityProtocol

  case class Ability(
    id: Long,
    name: String,
    order: Int
  )

  case class AbilityRsp(
    ability: Option[List[Ability]],
    errCode: Int = 0,
    msg: String = "ok"
  )

  case class AddAbilityReq(
    name: String
  )

  case class DeleteAbilityReq(
    id: Long
  )

  //  0表示上移，1表示下移
  case class SortAbilityReq(
    id: Long,
    order: Int,
    sortType: Int
  )


  //ApplyProtocol


  case class HaveUnreadRsp(
    isHaveUnread: Boolean,
    errCode: Int = 0,
    msg: String = "ok"
  )

  case class AddApplyReq(
    title: String,
    name: String,
    company: String,
    mobile: String,
    email: String,
    abilityId: List[Long]
  )

  case class Apply(
    id: Long,
    title: String,
    name: String,
    company: String,
    mobile: String,
    email: String,
    abilities: List[String],
    state: Int,
    readOrNot: Boolean,
    createTime: Long,
    userId : String
  )

  case class ApplyRsp(
    applys: Option[List[Apply]],
    errCode: Int = 0,
    msg: String = "ok"
  )

  case class DeleteApplyReq(
    applys: List[Long]
  )

  case class MarkApplyReq(
    id: Long
  )

  case class UnMarkApplyReq(
    id: Long
  )




}
