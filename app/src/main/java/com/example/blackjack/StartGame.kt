package com.example.blackjack

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.example.blackjack.databinding.ActivityStartGameBinding
import java.io.File

class StartGame : AppCompatActivity() {

    private lateinit var startBinding: ActivityStartGameBinding

    var game = Game()
    var end = false
    var credit = 100
    var bet = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startBinding = ActivityStartGameBinding.inflate(layoutInflater)
        val start = startBinding.root
        setContentView(start)

        game = Game()
        var p = Player("Ving")
        game.addPlayer(p)
        game.start()
        readData()
        showDealerCards()
        showPlayerCards()
        showCredit()

    }

    private fun showDealerCards(){
        var cardlayout = startBinding.dealerLayout
        cardlayout.removeAllViewsInLayout()

        var dealerhand = game.dealer.hand.cards

        for(c in dealerhand){
            var imageview1 = ImageView(this.applicationContext)
            imageview1.setImageResource(resources.getIdentifier(c.getImgName(),"drawable", this.packageName))
            cardlayout.addView(imageview1)

            //set layout
            imageview1.layoutParams.width = 150
            //imageview1.layoutParams.height = 250
        }
    }

    private fun showPlayerCards(){
        var playerhand = game.players[0].hand.cards

        var cardlayout = startBinding.cardlayout
        cardlayout.removeAllViewsInLayout()

        for(c in playerhand){
            var imageview1 = ImageView(this.applicationContext)
            imageview1.setImageResource(resources.getIdentifier(c.getImgName(),"drawable", this.packageName))

            cardlayout.addView(imageview1)

            //set layout
            imageview1.layoutParams.width = 150
            //imageview1.layoutParams.height = 250
        }
    }


    fun hitClick(view: View){
        if(!end){
            var c = game.dealer.dealCard()
            game.players[0].receiveCard(c)
            if(game.players[0].hand.value() > 21){
                endgame()
            }
            showPlayerCards()
        }
    }

    fun holdClick(view: View){
        endgame()
    }

    fun endgame(){

        game.end()
        showDealerCards()
        end = true

        showvalue()
        showRestart()
        showCredit()
        saveData()
    }

    private fun hidevalue(){
        var ptext = startBinding.playervaluetext
        var dtext = startBinding.dealervaluetext
        var ftext = startBinding.finaltext
        var btext = startBinding.bettext
        ptext.text = ""
        dtext.text = ""
        ftext.text = ""
        btext.text = ""
    }

    private fun showvalue(){
        var ptext = startBinding.playervaluetext
        var pvalue = game.players[0].hand

        if (pvalue.value() <= 21) {
            if(pvalue.isBlackJack()) {
                ptext.text = "BlackJack"
            } else {
                ptext.text = pvalue.value().toString()
            }
        } else {
            ptext.text = pvalue.value().toString() + " Over"
        }


        var dtext = startBinding.dealervaluetext
        var dvalue = game.dealer.hand
        if (dvalue.value() <= 21) {
            if(dvalue.isBlackJack()){
                dtext.text = "BlackJack"
            } else {
                dtext.text = dvalue.value().toString()
            }
        } else {
            dtext.text = dvalue.value().toString() + " Over"
        }

        var ftext = startBinding.finaltext

        var result = pvalue.compare(dvalue)
        when(result){
            1 ->{
                ftext.text = "Player Win"
                credit += 2*bet
                bet = 0
            }
            0 -> {
                ftext.text = "Draw"
                credit += bet
                bet = 0
            }
            -1 -> {
                ftext.text = "Player Lose"
                bet = 0
            }
        }

    }

    fun restartClick(view: View){
        end = false
        game.restart()
        hidevalue()

        var rbutton = startBinding.restartbutton
        rbutton.visibility = View.INVISIBLE

        showDealerCards()
        showPlayerCards()
        showCredit()
    }

    fun showRestart(){
        var rbutton = startBinding.restartbutton
        rbutton.visibility = View.VISIBLE

    }

    fun enterBet(view: View){

        if(bet < 1){
            credit -= 10
            bet += 10
        }
        showCredit()
    }

    fun showCredit(){
        var btext = startBinding.bettext
        var ctext = startBinding.credittext


        btext.text = "Bet: ${bet.toString()}"
        ctext.text = "Credit: ${credit.toString()}"

    }

    fun saveData(){
        val file = File(this.applicationContext.filesDir, "savedata")

        file.writeText(credit.toString())

    }

    fun readData(){
        val file = File(this.applicationContext.filesDir, "savedata")
        if(file.exists()) {
            val content = file.readText()
            credit = content.toInt()
        }
    }
}