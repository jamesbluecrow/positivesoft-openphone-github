package club.positivesoft.openphone.github.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import club.positivesoft.openphone.github.R
import club.positivesoft.openphone.github.ui.theme.DefaultTheme
import coil.compose.AsyncImage

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun UsersListScreen(
    viewModel: UsersListViewModel = hiltViewModel(),
    scaffoldState: ScaffoldState = rememberScaffoldState()
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Scaffold(scaffoldState = scaffoldState, modifier = Modifier.fillMaxSize()) {
        val query = rememberSaveable { viewModel.currentQuery }
        UsersListContent(
            viewModel.isLoading.value,
            query.value,
            onQueryChange = { viewModel.onQueryChange(it) },
            onSubmitSearch = {
                viewModel.onSubmitSearch(it)
                keyboardController?.hide()
            },
            viewModel.users
        )

        viewModel.infoMessage.value?.let {
            LaunchedEffect(scaffoldState.snackbarHostState) {
                scaffoldState.snackbarHostState.showSnackbar(message = it)
                viewModel.onSnackbarMessageShown()
            }
        }
    }
}

@Composable
fun UsersListContent(
    isLoading: Boolean,
    query: String,
    onQueryChange: (String) -> Unit,
    onSubmitSearch: (String) -> Unit,
    users: List<UserViewData>
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 16.dp)) {
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                placeholder = {
                    Text(text = "Search something")
                },
                keyboardActions = KeyboardActions { onSubmitSearch(query) })
            FloatingActionButton(
                onClick = { onSubmitSearch(query) },
                contentColor = MaterialTheme.colors.background,
                content = {
                    Icon(imageVector = Icons.Filled.Search, contentDescription = "Search button.")
                },
                modifier = Modifier.padding(16.dp, 0.dp, 0.dp, 0.dp)
            )
        }
        if (isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        LazyColumn {
            items(users) { user ->
                UserListItem(user = user)
            }
        }
    }
}

@Composable
private fun UserListItem(user: UserViewData) {
    Card(
        modifier = Modifier
            .padding(4.dp, 4.dp)
            .fillMaxWidth()
            .height(80.dp), shape = RoundedCornerShape(4.dp), elevation = 4.dp
    ) {
        Surface(color = MaterialTheme.colors.background) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = dimensionResource(id = R.dimen.horizontal_margin),
                        vertical = dimensionResource(id = R.dimen.list_item_padding),
                    )
            ) {
                AsyncImage(model = user.image, contentDescription = "User avatar url.")
                Column {
                    Text(
                        text = user.name,
                        style = MaterialTheme.typography.h6,
                        modifier = Modifier.padding(start = dimensionResource(id = R.dimen.horizontal_margin))
                    )
                    Text(
                        text = "Repositories: ${user.publicRepos}",
                        style = MaterialTheme.typography.subtitle1,
                        modifier = Modifier.padding(start = dimensionResource(id = R.dimen.horizontal_margin))
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    DefaultTheme {
        UsersListScreen()
    }
}