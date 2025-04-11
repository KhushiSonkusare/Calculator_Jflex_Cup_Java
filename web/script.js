document.addEventListener('DOMContentLoaded', function() {
    const expressionInput = document.getElementById('expression');
    const resultValue = document.getElementById('result-value');
    const historyContainer = document.getElementById('history');
    const calculateButton = document.getElementById('calculate');
    const clearButton = document.getElementById('clear');
    const backspaceButton = document.getElementById('backspace');
    const numberButtons = document.querySelectorAll('.number');
    const operatorButtons = document.querySelectorAll('.operator');
    const parenthesisButtons = document.querySelectorAll('.parenthesis');
    
    // Add click event listeners to buttons
    numberButtons.forEach(button => {
        button.addEventListener('click', () => {
            appendToExpression(button.getAttribute('data-value'));
        });
    });
    
    operatorButtons.forEach(button => {
        button.addEventListener('click', () => {
            appendToExpression(button.getAttribute('data-value'));
        });
    });
    
    parenthesisButtons.forEach(button => {
        button.addEventListener('click', () => {
            appendToExpression(button.getAttribute('data-value'));
        });
    });
    
    // Calculate button
    calculateButton.addEventListener('click', () => {
        calculate();
    });
    
    // Clear button
    clearButton.addEventListener('click', () => {
        expressionInput.value = '';
        expressionInput.focus();
    });
    
    // Backspace button
    backspaceButton.addEventListener('click', () => {
        expressionInput.value = expressionInput.value.slice(0, -1);
        expressionInput.focus();
    });
    
    // Enter key to calculate
    expressionInput.addEventListener('keydown', (e) => {
        if (e.key === 'Enter') {
            e.preventDefault();
            calculate();
        }
    });
    
    // Functions
    function appendToExpression(value) {
        expressionInput.value += value;
        expressionInput.focus();
    }
    
    function calculate() {
        const expression = expressionInput.value.trim();
        
        if (!expression) {
            showError('Please enter an expression');
            return;
        }
        
        // Send the expression to the server for calculation
        fetch('/api/calculate', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ expression: expression })
        })
        .then(response => response.json())
        .then(data => {
            if (data.error) {
                showError(data.error);
                return;
            }
            
            // Display the result
            resultValue.textContent = data.result;
            
            // Add to history
            addToHistory(expression, data.result);
            
            // Clear input and refocus
            expressionInput.value = '';
            expressionInput.focus();
        })
        .catch(error => {
            showError('Server error: ' + error.message);
        });
    }
    
    function showError(message) {
        resultValue.textContent = 'Error';
        alert(message);
    }
    
    function addToHistory(expression, result) {
        const historyItem = document.createElement('div');
        historyItem.className = 'history-item';
        historyItem.textContent = `${expression} = ${result}`;
        
        // Add click event to reuse the expression
        historyItem.addEventListener('click', () => {
            expressionInput.value = expression;
            expressionInput.focus();
        });
        
        historyContainer.prepend(historyItem);
        
        // Limit history items
        const historyItems = historyContainer.querySelectorAll('.history-item');
        if (historyItems.length > 10) {
            historyContainer.removeChild(historyItems[historyItems.length - 1]);
        }
    }
});