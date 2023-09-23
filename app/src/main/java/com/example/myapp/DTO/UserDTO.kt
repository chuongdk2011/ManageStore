package com.example.myapp.DTO

data class UserDTO(
    var id:String = "",
    var fullname:String = "",
    var pass:String = "",
    var email:String = "",
    var avt:String = "",
    var role:String = "Admin",
    var address:String ="",
    var phone:String = ""
    ) {
}