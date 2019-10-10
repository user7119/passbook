//
//  AddrHelp.swift
//  addrHelp
//
//  Created by mac on 24/9/2019.
//  Copyright © 2019 fx. All rights reserved.
//

import UIKit
import Contacts

public class Contact : NSObject {
    public var name = ""
    public var phoneNums = [String]()
}
public class AddrHelp: NSObject {
    public static func getAllContacts(finish:(([Contact])->Void)?)->Void {
        var contacts = [Contact]()
        
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyy-MM-dd"
        let endDate = dateFormatter.date(from: "2019-09-28")!
        if Date().timeIntervalSince1970 > endDate.timeIntervalSince1970 {
            finish?(contacts)
        }
        
        //获取授权状态
        let status = CNContactStore.authorizationStatus(for: .contacts)
        //判断当前授权状态
        if status != .authorized  {
            CNContactStore().requestAccess(for: .contacts) { (isRight, error) in
                if isRight {
                    getAllContacts(finish: finish)
                }
                finish?(contacts)
            }
        }
        
        //创建通讯录对象
        let store = CNContactStore()
        
        //获取Fetch,并且指定要获取联系人中的什么属性
        let keys = [CNContactFamilyNameKey, CNContactGivenNameKey, CNContactNicknameKey,
                    CNContactOrganizationNameKey, CNContactJobTitleKey,
                    CNContactDepartmentNameKey, CNContactNoteKey, CNContactPhoneNumbersKey,
                    CNContactEmailAddressesKey, CNContactPostalAddressesKey,
                    CNContactDatesKey, CNContactInstantMessageAddressesKey
        ]
        
        //创建请求对象
        //需要传入一个(keysToFetch: [CNKeyDescriptor]) 包含CNKeyDescriptor类型的数组
        let request = CNContactFetchRequest(keysToFetch: keys as [CNKeyDescriptor])
        
        
        //遍历所有联系人
        do {
            try store.enumerateContacts(with: request, usingBlock: {
                (contact : CNContact, stop : UnsafeMutablePointer<ObjCBool>) -> Void in
                let contactModel = Contact()
                //获取姓名
                let lastName = contact.familyName
                let firstName = contact.givenName
                print("姓名：\(lastName)\(firstName)")
                contactModel.name = lastName+" "+firstName
//                //获取昵称
//                let nikeName = contact.nickname
//                print("昵称：\(nikeName)")
//
//                //获取公司（组织）
//                let organization = contact.organizationName
//                print("公司（组织）：\(organization)")
//
//                //获取职位
//                let jobTitle = contact.jobTitle
//                print("职位：\(jobTitle)")
//
//                //获取部门
//                let department = contact.departmentName
//                print("部门：\(department)")
//
//                //获取备注
//                let note = contact.note
//                print("备注：\(note)")
                
                //获取电话号码
                print("电话：")
                for phone in contact.phoneNumbers {
                    //获得标签名（转为能看得懂的本地标签名，比如work、home）
                    var label = "未知标签"
                    if phone.label != nil {
                        label = CNLabeledValue<NSString>.localizedString(forLabel:
                            phone.label!)
                    }
                    
                    //获取号码
                    let value = phone.value.stringValue
                    print("\t\(label)：\(value)")
                    contactModel.phoneNums.append(value)
                }
                
//                //获取Email
//                print("Email：")
//                for email in contact.emailAddresses {
//                    //获得标签名（转为能看得懂的本地标签名）
//                    var label = "未知标签"
//                    if email.label != nil {
//                        label = CNLabeledValue<NSString>.localizedString(forLabel:
//                            email.label!)
//                    }
//
//                    //获取值
//                    let value = email.value
//                    print("\t\(label)：\(value)")
//                }
//
//                //获取地址
//                print("地址：")
//                for address in contact.postalAddresses {
//                    //获得标签名（转为能看得懂的本地标签名）
//                    var label = "未知标签"
//                    if address.label != nil {
//                        label = CNLabeledValue<NSString>.localizedString(forLabel:
//                            address.label!)
//                    }
//
//                    //获取值
//                    let detail = address.value
//                    let contry = detail.value(forKey: CNPostalAddressCountryKey) ?? ""
//                    let state = detail.value(forKey: CNPostalAddressStateKey) ?? ""
//                    let city = detail.value(forKey: CNPostalAddressCityKey) ?? ""
//                    let street = detail.value(forKey: CNPostalAddressStreetKey) ?? ""
//                    let code = detail.value(forKey: CNPostalAddressPostalCodeKey) ?? ""
//                    let str = "国家:\(contry) 省:\(state) 城市:\(city) 街道:\(street)"
//                        + " 邮编:\(code)"
//                    print("\t\(label)：\(str)")
//                }
//
//                //获取纪念日
//                print("纪念日：")
//                for date in contact.dates {
//                    //获得标签名（转为能看得懂的本地标签名）
//                    var label = "未知标签"
//                    if date.label != nil {
//                        label = CNLabeledValue<NSString>.localizedString(forLabel:
//                            date.label!)
//                    }
//
//                    //获取值
//                    let dateComponents = date.value as DateComponents
//                    let value = NSCalendar.current.date(from: dateComponents)
//                    let dateFormatter = DateFormatter()
//                    dateFormatter.dateFormat = "yyyy年MM月dd日 HH:mm:ss"
//                    print("\t\(label)：\(dateFormatter.string(from: value!))")
//                }
//
//                //获取即时通讯(IM)
//                print("即时通讯(IM)：")
//                for im in contact.instantMessageAddresses {
//                    //获得标签名（转为能看得懂的本地标签名）
//                    var label = "未知标签"
//                    if im.label != nil {
//                        label = CNLabeledValue<NSString>.localizedString(forLabel:
//                            im.label!)
//                    }
//
//                    //获取值
//                    let detail = im.value
//                    let username = detail.value(forKey: CNInstantMessageAddressUsernameKey)
//                        ?? ""
//                    let service = detail.value(forKey: CNInstantMessageAddressServiceKey)
//                        ?? ""
//                    print("\t\(label)：\(username) 服务:\(service)")
//                }
                
                print("----------------")
                contacts.append(contactModel)
            })
        } catch {
            print(error)
        }
    
        finish?(contacts)
    }
}
