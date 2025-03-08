package com.galacticware.griddle.android.activity

/*import org.predict4all.nlp.language.LanguageModel
import org.predict4all.nlp.language.french.FrenchDefaultCorrectionRuleGenerator
import org.predict4all.nlp.language.french.FrenchLanguageModel
import org.predict4all.nlp.ngram.dictionary.DynamicNGramDictionary
import org.predict4all.nlp.ngram.dictionary.StaticNGramTrieDictionary
import org.predict4all.nlp.prediction.PredictionParameter
import org.predict4all.nlp.prediction.WordPredictor
import org.predict4all.nlp.words.WordDictionary
import org.predict4all.nlp.words.correction.CorrectionRule
import org.predict4all.nlp.words.correction.CorrectionRuleNode
import org.predict4all.nlp.words.correction.CorrectionRuleNodeType*/
import android.content.Context
import android.graphics.Canvas
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.core.app.ActivityCompat
import androidx.datastore.core.DataStore
import androidx.navigation.compose.rememberNavController
import com.galacticware.griddle.domain.view.colorization.GriddleTheme
import com.galacticware.griddle.domain.view.navigation.GriddleAppNavigation
import com.galacticware.griddle.domain.view.observers.ScreenChangeObserver
import java.io.File
import java.util.prefs.Preferences


class MainActivity : AppCompatActivity() {
    private val screenChangeObserver = ScreenChangeObserver()
    private lateinit var dataStore: DataStore<Preferences>
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

       /*
        checkDefaultFileExist(this)
        val languageModel: LanguageModel = FrenchLanguageModel()
        val predictionParameter = PredictionParameter(languageModel)
        // Configure correction rules

        // Configure correction rules
        configureCorrectionRules(predictionParameter)


        // Load dictionary (and user dictionary if exists)
        val wordDictionary = WordDictionary.loadDictionary(languageModel, FILE_WORDS)

        // Load dynamic ngram (if exists) or create a new one
        val dynamicNGramDictionary: DynamicNGramDictionary =             DynamicNGramDictionary(4);
        val staticNGramTrieDictionary = StaticNGramTrieDictionary.open(FILE_NGRAMS)
        val wordPredictor = WordPredictor(predictionParameter,
            wordDictionary,
            staticNGramTrieDictionary,
            dynamicNGramDictionary
        )

        println("Dynamic ngram model loaded/created");

        IMEService.nextWordPredictor = wordPredictor*/

        lifecycle.addObserver(screenChangeObserver)

        ActivityCompat.requestPermissions(this, arrayOf(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ), 1)

        setContent {
            // Instantiate NavController at the top level
            val navController = rememberNavController()
            val griddleAppNavigation = remember {
                GriddleAppNavigation(navController, applicationContext)
            }
            GriddleTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    griddleAppNavigation.Navigate()
                }
            }
        }
    }
    companion object {
        val TAG = "com.galacticware.griddle"
        var FILE_NGRAMS: File? = null
        var FILE_WORDS: File? = null

/*
        private fun configureCorrectionRules(predictionParameter: PredictionParameter) {
            // Add some default rules : simulate kind of GBoard Android behavior
            val root = CorrectionRuleNode(CorrectionRuleNodeType.NODE)
            root.addChild(FrenchDefaultCorrectionRuleGenerator.CorrectionRuleType.ACCENTS.generateNodeFor(
                predictionParameter))
            root.addChild(FrenchDefaultCorrectionRuleGenerator.CorrectionRuleType.WORD_SPACE_APOSTROPHE.generateNodeFor(
                predictionParameter))

            // Add some default rules : add language specific rules
            root.addChild(FrenchDefaultCorrectionRuleGenerator.CorrectionRuleType.M_FRONT_MBP.generateNodeFor(
                predictionParameter))
            root.addChild(FrenchDefaultCorrectionRuleGenerator.CorrectionRuleType.WORD_ENDINGS.generateNodeFor(
                predictionParameter))

            // Simulate a custom rule add : our user sometimes forget "h" at the word start (like "homme")
            val hRule = CorrectionRuleNode(CorrectionRuleNodeType.LEAF)
            hRule.setCorrectionRule(CorrectionRule.ruleBuilder().withError(TAG).withReplacement("h")
                .withMaxIndexFromStart(1))
            root.addChild(hRule)

            // Simulate a custom rule : our user is always confused between "ai" and "é" and "è"
            val eaiConfusionRule = CorrectionRuleNode(CorrectionRuleNodeType.LEAF)
            eaiConfusionRule.setCorrectionRule(CorrectionRule.ruleBuilder()
                .withConfusionSet("ai", "é", "è"))
            root.addChild(eaiConfusionRule)

            // Set information on prediction parameters
            predictionParameter.setCorrectionRulesRoot(root)
            predictionParameter.setEnableWordCorrection(true)
        }
*/

        private fun checkDefaultFileExist(context: Context) {
            if (FILE_NGRAMS == null || FILE_WORDS == null) {
                FILE_NGRAMS = context.assets.open("prediction/language/french/fr_ngrams.bin").let { inputStream ->
                    File.createTempFile("fr_ngrams", ".bin").apply {
                        inputStream.copyTo(outputStream())
                    }
                }
                FILE_WORDS = context.assets.open("prediction/language/french/fr_words.bin").let { inputStream ->
                    File.createTempFile("fr_words", ".bin").apply {
                        inputStream.copyTo(outputStream())
                    }
                }
            }
        }
    }
}

class InvisibleUpdatingCanvas(ctx: Context) {
    private val _instance = Canvas().apply {
        
    }
}
