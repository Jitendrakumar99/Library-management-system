// Popup Message Function
function showPopup(message, type) {
    // Remove existing popup if any
    const existingPopup = document.getElementById('messagePopup');
    if (existingPopup) {
        existingPopup.remove();
    }

    // Create popup element
    const popup = document.createElement('div');
    popup.id = 'messagePopup';
    popup.className = `popup-message popup-${type}`;
    popup.innerHTML = `
        <div class="popup-content">
            <span class="popup-icon">${type === 'success' ? '✓' : '✕'}</span>
            <span class="popup-text">${message}</span>
        </div>
    `;

    // Add to body
    document.body.appendChild(popup);

    // Trigger animation
    setTimeout(() => {
        popup.classList.add('show');
    }, 10);

    // Auto remove after 3 seconds
    setTimeout(() => {
        popup.classList.remove('show');
        setTimeout(() => {
            popup.remove();
        }, 300);
    }, 3000);
}

// Check for flash messages on page load
document.addEventListener('DOMContentLoaded', function() {
    // Check for success message
    const successMsg = document.getElementById('successMessage')?.textContent || 
                      document.querySelector('[data-success-message]')?.getAttribute('data-success-message');
    if (successMsg) {
        showPopup(successMsg, 'success');
    }

    // Check for error message
    const errorMsg = document.getElementById('errorMessage')?.textContent || 
                    document.querySelector('[data-error-message]')?.getAttribute('data-error-message');
    if (errorMsg) {
        showPopup(errorMsg, 'error');
    }

    // Check URL parameters
    const urlParams = new URLSearchParams(window.location.search);
    if (urlParams.get('login') === 'success') {
        showPopup('Login successful! Welcome back.', 'success');
        // Clean URL
        window.history.replaceState({}, document.title, window.location.pathname);
    }
    if (urlParams.get('logout') === 'true') {
        showPopup('You have been logged out successfully.', 'success');
        // Clean URL
        window.history.replaceState({}, document.title, window.location.pathname);
    }
});

