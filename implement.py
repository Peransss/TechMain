import sys

def main():
    # Update BattleViewModel
    with open('app/src/main/java/com/example/techmain/ui/battle/BattleViewModel.kt', 'r') as f:
        content = f.read()

    # Add featuredQuizzes StateFlow
    if 'private val _featuredQuizzes = MutableStateFlow<List<CustomQuiz>>(emptyList())' not in content:
        content = content.replace(
            'val state: StateFlow<BattleUiState> = _state.asStateFlow()',
            'val state: StateFlow<BattleUiState> = _state.asStateFlow()\n\n    private val _featuredQuizzes = MutableStateFlow<List<CustomQuiz>>(emptyList())\n    val featuredQuizzes: StateFlow<List<CustomQuiz>> = _featuredQuizzes.asStateFlow()'
        )

    # Initialize in init
    if 'firestore.fetchFeaturedQuizzes().collect' not in content:
        content = content.replace(
            'val userId = FirebaseModule.getUserId() ?: return',
            'val userId = FirebaseModule.getUserId() ?: return\n        viewModelScope.launch {\n            firestore.fetchFeaturedQuizzes().collect { _featuredQuizzes.value = it }\n        }'
        )
    
    # Imports
    if 'import com.example.techmain.firebase.CustomQuiz' not in content:
        content = content.replace('import com.example.techmain.firebase.FirebaseModule', 'import com.example.techmain.firebase.FirebaseModule\nimport com.example.techmain.firebase.CustomQuiz')

    with open('app/src/main/java/com/example/techmain/ui/battle/BattleViewModel.kt', 'w') as f:
        f.write(content)

    print("BattleViewModel updated")

if __name__ == '__main__':
    main()
