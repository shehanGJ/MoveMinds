import { useEffect } from 'react';

export const useDropdownLock = () => {
  useEffect(() => {
    const handleDropdownOpen = () => {
      document.body.classList.add('dropdown-open');
    };

    const handleDropdownClose = () => {
      document.body.classList.remove('dropdown-open');
    };

    // Listen for dropdown state changes
    const observer = new MutationObserver((mutations) => {
      mutations.forEach((mutation) => {
        if (mutation.type === 'attributes' && mutation.attributeName === 'data-state') {
          const target = mutation.target as HTMLElement;
          if (target.getAttribute('data-state') === 'open') {
            handleDropdownOpen();
          } else {
            handleDropdownClose();
          }
        }
      });
    });

    // Observe all elements with data-state attribute
    const elements = document.querySelectorAll('[data-state]');
    elements.forEach((element) => {
      observer.observe(element, { attributes: true, attributeFilter: ['data-state'] });
    });

    return () => {
      observer.disconnect();
      document.body.classList.remove('dropdown-open');
    };
  }, []);
};
