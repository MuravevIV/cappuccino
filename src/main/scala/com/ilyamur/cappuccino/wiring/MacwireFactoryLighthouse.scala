package com.ilyamur.cappuccino.wiring

import com.softwaremill.macwire._

/**
  * This example shows how MacWire can be used to provide factory-dependencies with minimal boilerplate.
  */
object MacwireFactoryLighthouse extends App {


  // ======== INTERFACES ========

  object User {

    // create user instance by providing its email address
    type Factory = (String) => User
  }

  trait User {

    // user encapsulates email sending functionality
    def sendEmail(text: String)
  }

  trait EmailService {

    // email service is responsible for sending emails only
    def sendEmail(address: String, text: String)
  }

  trait Spammer {

    // spammer knows a list of addresses and needs to send emails to all of them with provided text
    def sendEmails(text: String)
  }


  // ======== IMPLEMENTATIONS ========

  // only knows about user address and email service (as a trait)
  class AppUser(address: String,
                emailService: EmailService) extends User {

    override def sendEmail(text: String): Unit = {
      // send email using abstract email service
      emailService.sendEmail(address, text)
    }
  }

  // sends emails (in console output), no dependencies
  class AppEmailService extends EmailService {

    override def sendEmail(address: String, text: String): Unit = {
      // stub example
      println(s"Sending email to '${address}': '${text}")
    }
  }

  // requires abstract user factory to create users based on their addresses
  class AppSpammer(userFactory: User.Factory) extends Spammer {

    private val addresses = List(
      "dave@mail.com",
      "john@mail.com",
      "mike@mail.com"
    )

    override def sendEmails(text: String): Unit = {
      addresses.foreach { address =>
        // create user using factory
        val user = userFactory.apply(address)
        // send email to user (user implementation itself will decide how it will be done)
        user.sendEmail(text)
      }
    }
  }


  // ======== WIRING ========

  class ApplicationModule {

    // wiring for User.Factory - this is possible because scala functions are output-covariant, macros expanded to:
    //
    // lazy val userFactory = (address: String) => new AppUser(address, emailService)
    //
    lazy val userFactory = (address: String) => wire[AppUser]

    // wiring for EmailService, macros expanded to:
    //
    // lazy val emailService = new AppEmailService()
    //
    lazy val emailService = wire[AppEmailService]

    // wiring for Spammer, macros expanded to:
    //
    // lazy val spammer = new AppSpammer(userFactory)
    //
    lazy val spammer = wire[AppSpammer]
  }


  // ======== EXECUTION ========

  val applicationModule = new ApplicationModule()

  applicationModule.spammer.sendEmails("Free Adderall!")
}
