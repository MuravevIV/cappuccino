package com.ilyamur.cappuccino.wiring

import com.softwaremill.macwire._
import com.typesafe.config.{Config, ConfigFactory}
import org.slf4j.LoggerFactory

object MacwireFactoryLighthouse extends App {

  trait User {

    def sendEmail(text: String)
  }

  trait UserFactory {

    def apply(address: String): User
  }

  object EmailService {

    class ConfigProvider(config: Config) {

      def apply(): Config = config.getConfig("email")
    }
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

      override def apply(address: String) = new AppUser(emailService, address)
    }
  }

  class AppUser(emailService: EmailService, address: String) extends User {

    override def sendEmail(text: String): Unit = {
      emailService.sendEmail(address, text)
    }
  }

  class AppEmailService(configProvider: EmailService.ConfigProvider) extends EmailService {

    private val config = configProvider()
    private val smtpServer = config.getString("smtp-server")

    private val log = LoggerFactory.getLogger(getClass)

    override def sendEmail(address: String, text: String): Unit = {
      log.info(s"Sending email to '${address}', using SMTP server '${smtpServer}': '${text}'")
    }
  }

  class AppSpammer(userFactory: UserFactory) extends Spammer {

    override def sendEmails(text: String): Unit = {
      userFactory("john@test.com").sendEmail(text)
      userFactory("jane@test.com").sendEmail(text)
    }
  }

  //

  trait ApplicationConfigs {

    val config = ConfigFactory.load()
    def emailServiceCProv = new EmailService.ConfigProvider(config)
  }

  trait ApplicationModule extends ApplicationConfigs {

    lazy val emailService = wire[AppEmailService]
    lazy val userFactory = wire[AppUser.Factory]
    lazy val spammer = wire[AppSpammer]
  }

  class Application extends App with ApplicationModule {

    spammer.sendEmails("Free Adderall!")
  }

  //

  (new Application()).main(args)
}
