package com.ilyamur.cappuccino.wiring

import com.softwaremill.macwire._

object MacwireFactoryLighthouse extends App {

  trait User {

    def sendEmail(text: String)
  }

  trait UserFactory {

    def create(address: String): User
  }

  trait EmailService {

    def sendEmail(address: String, text: String)
  }

  trait Spammer {

    def sendEmails(text: String)
  }

  //

  object AppUser {

    class Factory(emailService: EmailService) extends UserFactory {

      def create(address: String) = new AppUser(emailService, address)
    }
  }

  class AppUser(emailService: EmailService, address: String) extends User {

    override def sendEmail(text: String): Unit = {
      emailService.sendEmail(address, text)
    }
  }

  class AppEmailService extends EmailService {

    override def sendEmail(address: String, text: String): Unit = {
      println(s"Sending email to '${address}': '${text}")
    }
  }

  class AppSpammer(userFactory: UserFactory) extends Spammer {

    override def sendEmails(text: String): Unit = {
      userFactory.create("john@test.com").sendEmail(text)
      userFactory.create("jane@test.com").sendEmail(text)
    }
  }

  //

  class ApplicationModule {

    lazy val appEmailService = wire[AppEmailService]
    lazy val appUserFactory = wire[AppUser.Factory]
    lazy val appSpammer = wire[AppSpammer]
  }

  //

  val app = new ApplicationModule()
  app.appSpammer.sendEmails("Free Adderall!")
}
