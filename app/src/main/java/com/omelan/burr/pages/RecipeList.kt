package com.omelan.burr.pages

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.viewModel
import com.omelan.burr.components.RecipeItem
import com.omelan.burr.model.RecipeViewModel
import com.omelan.burr.ui.BurrTheme
import dev.chrisbanes.accompanist.insets.AmbientWindowInsets
import dev.chrisbanes.accompanist.insets.navigationBarsPadding
import dev.chrisbanes.accompanist.insets.toPaddingValues

@Composable
fun RecipeList(
    navigateToRecipe: (recipeId: Int) -> Unit,
    addNewRecipe: () -> Unit,
    recipeViewModel: RecipeViewModel = viewModel(),
) {
    val recipes = recipeViewModel.getAllRecipes().observeAsState(initial = listOf())
    BurrTheme {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                    onClick = addNewRecipe,
                    modifier = Modifier.navigationBarsPadding()
                ) {
                    Icon(Icons.Rounded.Add)
                }
            },
        ) {
            LazyColumn(
                contentPadding = AmbientWindowInsets.current.navigationBars.toPaddingValues(),
                modifier = Modifier.fillMaxSize()
            ) {
                items(recipes.value) { recipe ->
                    RecipeItem(
                        recipe = recipe,
                        onPress = navigateToRecipe,
                    )
                }
            }
        }
    }


}

@Preview
@Composable
fun RecipeListPreview() {
    RecipeList(navigateToRecipe = {}, addNewRecipe = {})
}