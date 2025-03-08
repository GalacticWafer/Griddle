package com.galacticware.griddle.android.dagger

import com.galacticware.griddle.domain.model.textreplacement.TextReplacementViewModel
import dagger.Component
import com.galacticware.griddle.domain.view.KeyboardView
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(target: KeyboardView)
    fun inject(textReplacementViewModel: TextReplacementViewModel)
}
