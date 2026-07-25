/**
 * Offline Tailwind CSS Play Engine for Android WebView
 * Intercepts https://cdn.tailwindcss.com and dynamically builds/injects styles for Tailwind classes offline.
 */
(function() {
  if (window.tailwind) return;

  const styleElement = document.createElement('style');
  styleElement.id = 'offline-tailwind-styles';
  document.head.appendChild(styleElement);

  const generatedClasses = new Set();
  let cssRules = [];

  // Core color map
  const colors = {
    slate: { 50: '#f8fafc', 100: '#f1f5f9', 200: '#e2e8f0', 300: '#cbd5e1', 400: '#94a3b8', 500: '#64748b', 600: '#475569', 700: '#334155', 800: '#1e293b', 900: '#0f172a', 950: '#020617' },
    gray: { 50: '#f9fafb', 100: '#f3f4f6', 200: '#e5e7eb', 300: '#d1d5db', 400: '#9ca3af', 500: '#6b7280', 600: '#4b5563', 700: '#374151', 800: '#1f2937', 900: '#111827', 950: '#030712' },
    zinc: { 50: '#fafafa', 100: '#f4f4f5', 200: '#e4e4e7', 300: '#d4d4d8', 400: '#a1a1aa', 500: '#71717a', 600: '#52525b', 700: '#3f3f46', 800: '#27272a', 900: '#18181b', 950: '#09090b' },
    red: { 50: '#fef2f2', 100: '#fee2e2', 200: '#fecaca', 300: '#fca5a5', 400: '#f87171', 500: '#ef4444', 600: '#dc2626', 700: '#b91c1c', 800: '#991b1b', 900: '#7f1d1d', 950: '#450a0a' },
    orange: { 50: '#fff7ed', 100: '#ffedd5', 200: '#fed7aa', 300: '#fdba74', 400: '#fb923c', 500: '#f97316', 600: '#ea580c', 700: '#c2410c', 800: '#9a3412', 900: '#7c2d12', 950: '#431407' },
    amber: { 50: '#fffbeb', 100: '#fef3c7', 200: '#fde68a', 300: '#fcd34d', 400: '#fbbf24', 500: '#f59e0b', 600: '#d97706', 700: '#b45309', 800: '#92400e', 900: '#78350f', 950: '#451a03' },
    emerald: { 50: '#ecfdf5', 100: '#d1fae5', 200: '#a7f3d0', 300: '#6ee7b7', 400: '#34d399', 500: '#10b981', 600: '#059669', 700: '#047857', 800: '#065f46', 900: '#064e3b', 950: '#022c22' },
    blue: { 50: '#eff6ff', 100: '#dbeafe', 200: '#bfdbfe', 300: '#93c5fd', 400: '#60a5fa', 500: '#3b82f6', 600: '#2563eb', 700: '#1d4ed8', 800: '#1e40af', 900: '#1e3a8a', 950: '#172554' },
    indigo: { 50: '#eef2ff', 100: '#e0e7ff', 200: '#c7d2fe', 300: '#a5b4fc', 400: '#818cf8', 500: '#6366f1', 600: '#4f46e5', 700: '#4338ca', 800: '#3730a3', 900: '#312e81', 950: '#1e1b4b' },
    violet: { 50: '#f5f3ff', 100: '#ede9fe', 200: '#ddd6fe', 300: '#c4b5fd', 400: '#a78bfa', 500: '#8b5cf6', 600: '#7c3aed', 700: '#6d28d9', 800: '#5b21b6', 900: '#4c1d95', 950: '#2e1065' },
    purple: { 50: '#faf5ff', 100: '#f3e8ff', 200: '#e9d5ff', 300: '#d8b4fe', 400: '#c084fc', 500: '#a855f7', 600: '#9333ea', 700: '#7e22ce', 800: '#6b21a8', 900: '#581c87', 950: '#3b0764' },
    pink: { 50: '#fdf2f8', 100: '#fce7f3', 200: '#fbcfe8', 300: '#f9a8d4', 400: '#f472b6', 500: '#ec4899', 600: '#db2777', 700: '#be185d', 800: '#9d174d', 900: '#831843', 950: '#500724' },
    white: '#ffffff',
    black: '#000000',
    transparent: 'transparent',
    current: 'currentColor'
  };

  function getColor(name, shade) {
    if (colors[name]) {
      if (typeof colors[name] === 'object') {
        return colors[name][shade] || colors[name][500];
      }
      return colors[name];
    }
    return null;
  }

  function escapeClass(cls) {
    return cls.replace(/([:\/\[\]%#.#()])/g, '\\$1');
  }

  function parseClass(cls) {
    if (generatedClasses.has(cls)) return;
    generatedClasses.add(cls);

    let prefix = '';
    let targetClass = cls;

    // Handle dark: or hover: or focus: prefixes
    const parts = cls.split(':');
    if (parts.length > 1) {
      prefix = parts.slice(0, -1).join(':');
      targetClass = parts[parts.length - 1];
    }

    let ruleBody = '';

    // Layout
    if (targetClass === 'flex') ruleBody = 'display: flex;';
    else if (targetClass === 'inline-flex') ruleBody = 'display: inline-flex;';
    else if (targetClass === 'grid') ruleBody = 'display: grid;';
    else if (targetClass === 'block') ruleBody = 'display: block;';
    else if (targetClass === 'inline-block') ruleBody = 'display: inline-block;';
    else if (targetClass === 'hidden') ruleBody = 'display: none;';
    else if (targetClass === 'flex-col') ruleBody = 'flex-direction: column;';
    else if (targetClass === 'flex-row') ruleBody = 'flex-direction: row;';
    else if (targetClass === 'flex-wrap') ruleBody = 'flex-wrap: wrap;';
    else if (targetClass === 'flex-1') ruleBody = 'flex: 1 1 0%;';
    else if (targetClass === 'items-center') ruleBody = 'align-items: center;';
    else if (targetClass === 'items-start') ruleBody = 'align-items: flex-start;';
    else if (targetClass === 'items-end') ruleBody = 'align-items: flex-end;';
    else if (targetClass === 'justify-center') ruleBody = 'justify-content: center;';
    else if (targetClass === 'justify-between') ruleBody = 'justify-content: space-between;';
    else if (targetClass === 'justify-start') ruleBody = 'justify-content: flex-start;';
    else if (targetClass === 'justify-end') ruleBody = 'justify-content: flex-end;';
    else if (targetClass === 'justify-around') ruleBody = 'justify-content: space-around;';
    
    // Grid columns
    else if (targetClass.startsWith('grid-cols-')) {
      const cols = targetClass.replace('grid-cols-', '');
      ruleBody = `grid-template-columns: repeat(${cols}, minmax(0, 1fr));`;
    }

    // Gap
    else if (targetClass.startsWith('gap-')) {
      const val = parseFloat(targetClass.replace('gap-', '')) * 0.25;
      ruleBody = `gap: ${val}rem;`;
    }

    // Padding
    else if (targetClass.startsWith('p-')) ruleBody = `padding: ${parseFloat(targetClass.replace('p-', '')) * 0.25}rem;`;
    else if (targetClass.startsWith('px-')) {
      const v = parseFloat(targetClass.replace('px-', '')) * 0.25;
      ruleBody = `padding-left: ${v}rem; padding-right: ${v}rem;`;
    } else if (targetClass.startsWith('py-')) {
      const v = parseFloat(targetClass.replace('py-', '')) * 0.25;
      ruleBody = `padding-top: ${v}rem; padding-bottom: ${v}rem;`;
    } else if (targetClass.startsWith('pt-')) ruleBody = `padding-top: ${parseFloat(targetClass.replace('pt-', '')) * 0.25}rem;`;
    else if (targetClass.startsWith('pb-')) ruleBody = `padding-bottom: ${parseFloat(targetClass.replace('pb-', '')) * 0.25}rem;`;
    else if (targetClass.startsWith('pl-')) ruleBody = `padding-left: ${parseFloat(targetClass.replace('pl-', '')) * 0.25}rem;`;
    else if (targetClass.startsWith('pr-')) ruleBody = `padding-right: ${parseFloat(targetClass.replace('pr-', '')) * 0.25}rem;`;

    // Margin
    else if (targetClass.startsWith('m-')) ruleBody = `margin: ${parseFloat(targetClass.replace('m-', '')) * 0.25}rem;`;
    else if (targetClass.startsWith('mx-')) {
      const v = targetClass === 'mx-auto' ? 'auto' : `${parseFloat(targetClass.replace('mx-', '')) * 0.25}rem`;
      ruleBody = `margin-left: ${v}; margin-right: ${v};`;
    } else if (targetClass.startsWith('my-')) {
      const v = parseFloat(targetClass.replace('my-', '')) * 0.25;
      ruleBody = `margin-top: ${v}rem; margin-bottom: ${v}rem;`;
    } else if (targetClass.startsWith('mt-')) ruleBody = `margin-top: ${parseFloat(targetClass.replace('mt-', '')) * 0.25}rem;`;
    else if (targetClass.startsWith('mb-')) ruleBody = `margin-bottom: ${parseFloat(targetClass.replace('mb-', '')) * 0.25}rem;`;

    // Sizing
    else if (targetClass === 'w-full') ruleBody = 'width: 100%;';
    else if (targetClass === 'w-screen') ruleBody = 'width: 100vw;';
    else if (targetClass === 'h-full') ruleBody = 'height: 100%;';
    else if (targetClass === 'h-screen') ruleBody = 'height: 100vh;';
    else if (targetClass.startsWith('w-')) {
      const num = parseFloat(targetClass.replace('w-', ''));
      if (!isNaN(num)) ruleBody = `width: ${num * 0.25}rem;`;
    } else if (targetClass.startsWith('h-')) {
      const num = parseFloat(targetClass.replace('h-', ''));
      if (!isNaN(num)) ruleBody = `height: ${num * 0.25}rem;`;
    } else if (targetClass.startsWith('max-w-')) {
      const val = targetClass.replace('max-w-', '');
      if (val === 'sm') ruleBody = 'max-width: 24rem;';
      else if (val === 'md') ruleBody = 'max-width: 28rem;';
      else if (val === 'lg') ruleBody = 'max-width: 32rem;';
      else if (val === 'xl') ruleBody = 'max-width: 36rem;';
      else if (val === '2xl') ruleBody = 'max-width: 42rem;';
      else if (val === '4xl') ruleBody = 'max-width: 56rem;';
      else if (val === 'full') ruleBody = 'max-width: 100%;';
    }

    // Colors: bg-, text-, border-
    else if (targetClass.startsWith('bg-')) {
      const colorVal = targetClass.replace('bg-', '');
      if (colorVal.startsWith('[')) {
        const customHex = colorVal.replace(/\[|\]/g, '');
        ruleBody = `background-color: ${customHex};`;
      } else {
        const cParts = colorVal.split('-');
        const cColor = getColor(cParts[0], cParts[1]);
        if (cColor) ruleBody = `background-color: ${cColor};`;
      }
    } else if (targetClass.startsWith('text-')) {
      const colorVal = targetClass.replace('text-', '');
      if (colorVal === 'xs') ruleBody = 'font-size: 0.75rem; line-height: 1rem;';
      else if (colorVal === 'sm') ruleBody = 'font-size: 0.875rem; line-height: 1.25rem;';
      else if (colorVal === 'base') ruleBody = 'font-size: 1rem; line-height: 1.5rem;';
      else if (colorVal === 'lg') ruleBody = 'font-size: 1.125rem; line-height: 1.75rem;';
      else if (colorVal === 'xl') ruleBody = 'font-size: 1.25rem; line-height: 1.75rem;';
      else if (colorVal === '2xl') ruleBody = 'font-size: 1.5rem; line-height: 2rem;';
      else if (colorVal === '3xl') ruleBody = 'font-size: 1.875rem; line-height: 2.25rem;';
      else if (colorVal === '4xl') ruleBody = 'font-size: 2.25rem; line-height: 2.5rem;';
      else if (colorVal === 'left') ruleBody = 'text-align: left;';
      else if (colorVal === 'center') ruleBody = 'text-align: center;';
      else if (colorVal === 'right') ruleBody = 'text-align: right;';
      else if (colorVal.startsWith('[')) {
        ruleBody = `color: ${colorVal.replace(/\[|\]/g, '')};`;
      } else {
        const cParts = colorVal.split('-');
        const cColor = getColor(cParts[0], cParts[1]);
        if (cColor) ruleBody = `color: ${cColor};`;
      }
    } else if (targetClass.startsWith('border-')) {
      const borderVal = targetClass.replace('border-', '');
      if (borderVal === '0') ruleBody = 'border-width: 0px;';
      else if (borderVal === '2') ruleBody = 'border-width: 2px;';
      else if (borderVal === '4') ruleBody = 'border-width: 4px;';
      else {
        const cParts = borderVal.split('-');
        const cColor = getColor(cParts[0], cParts[1]);
        if (cColor) ruleBody = `border-color: ${cColor}; border-style: solid; border-width: 1px;`;
        else ruleBody = 'border-style: solid; border-width: 1px;';
      }
    } else if (targetClass === 'border') {
      ruleBody = 'border-style: solid; border-width: 1px;';
    }

    // Typography
    else if (targetClass === 'font-sans') ruleBody = 'font-family: ui-sans-serif, system-ui, -apple-system, sans-serif;';
    else if (targetClass === 'font-serif') ruleBody = 'font-family: ui-serif, Georgia, Cambria, serif;';
    else if (targetClass === 'font-mono') ruleBody = 'font-family: ui-monospace, SFMono-Regular, monospace;';
    else if (targetClass === 'font-thin') ruleBody = 'font-weight: 100;';
    else if (targetClass === 'font-light') ruleBody = 'font-weight: 300;';
    else if (targetClass === 'font-normal') ruleBody = 'font-weight: 400;';
    else if (targetClass === 'font-medium') ruleBody = 'font-weight: 500;';
    else if (targetClass === 'font-semibold') ruleBody = 'font-weight: 600;';
    else if (targetClass === 'font-bold') ruleBody = 'font-weight: 700;';
    else if (targetClass === 'font-extrabold') ruleBody = 'font-weight: 800;';

    // Rounded / Borders
    else if (targetClass === 'rounded-none') ruleBody = 'border-radius: 0px;';
    else if (targetClass === 'rounded-sm') ruleBody = 'border-radius: 0.125rem;';
    else if (targetClass === 'rounded') ruleBody = 'border-radius: 0.25rem;';
    else if (targetClass === 'rounded-md') ruleBody = 'border-radius: 0.375rem;';
    else if (targetClass === 'rounded-lg') ruleBody = 'border-radius: 0.5rem;';
    else if (targetClass === 'rounded-xl') ruleBody = 'border-radius: 0.75rem;';
    else if (targetClass === 'rounded-2xl') ruleBody = 'border-radius: 1rem;';
    else if (targetClass === 'rounded-3xl') ruleBody = 'border-radius: 1.5rem;';
    else if (targetClass === 'rounded-full') ruleBody = 'border-radius: 9999px;';

    // Shadow
    else if (targetClass === 'shadow-sm') ruleBody = 'box-shadow: 0 1px 2px 0 rgb(0 0 0 / 0.05);';
    else if (targetClass === 'shadow') ruleBody = 'box-shadow: 0 1px 3px 0 rgb(0 0 0 / 0.1), 0 1px 2px -1px rgb(0 0 0 / 0.1);';
    else if (targetClass === 'shadow-md') ruleBody = 'box-shadow: 0 4px 6px -1px rgb(0 0 0 / 0.1), 0 2px 4px -2px rgb(0 0 0 / 0.1);';
    else if (targetClass === 'shadow-lg') ruleBody = 'box-shadow: 0 10px 15px -3px rgb(0 0 0 / 0.1), 0 4px 6px -4px rgb(0 0 0 / 0.1);';
    else if (targetClass === 'shadow-xl') ruleBody = 'box-shadow: 0 20px 25px -5px rgb(0 0 0 / 0.1), 0 8px 10px -6px rgb(0 0 0 / 0.1);';
    else if (targetClass === 'shadow-2xl') ruleBody = 'box-shadow: 0 25px 50px -12px rgb(0 0 0 / 0.25);';
    else if (targetClass === 'shadow-none') ruleBody = 'box-shadow: none;';

    // Transitions & Misc
    else if (targetClass === 'transition') ruleBody = 'transition-property: color, background-color, border-color, transform, opacity; transition-duration: 150ms;';
    else if (targetClass === 'duration-200') ruleBody = 'transition-duration: 200ms;';
    else if (targetClass === 'duration-300') ruleBody = 'transition-duration: 300ms;';
    else if (targetClass === 'hover:scale-105') ruleBody = 'transform: scale(1.05);';
    else if (targetClass === 'cursor-pointer') ruleBody = 'cursor: pointer;';
    else if (targetClass === 'overflow-hidden') ruleBody = 'overflow: hidden;';
    else if (targetClass === 'overflow-auto') ruleBody = 'overflow: auto;';

    if (ruleBody) {
      const escaped = escapeClass(cls);
      if (prefix === 'hover') {
        cssRules.push(`.${escaped}:hover { ${ruleBody} }`);
      } else if (prefix === 'focus') {
        cssRules.push(`.${escaped}:focus { ${ruleBody} }`);
      } else if (prefix === 'dark') {
        cssRules.push(`@media (prefers-color-scheme: dark) { .${escaped} { ${ruleBody} } }`);
      } else {
        cssRules.push(`.${escaped} { ${ruleBody} }`);
      }
    }
  }

  function scanDOM() {
    const elements = document.querySelectorAll('*');
    elements.forEach(el => {
      if (el.classList && el.classList.length) {
        el.classList.forEach(cls => parseClass(cls));
      }
    });
    styleElement.innerHTML = cssRules.join('\n');
  }

  // Observe changes
  const observer = new MutationObserver(() => scanDOM());
  document.addEventListener('DOMContentLoaded', () => {
    scanDOM();
    observer.observe(document.body, { childList: true, subtree: true, attributes: true, attributeFilter: ['class'] });
  });

  // Global Tailwind stub
  window.tailwind = {
    config: {},
    refresh: scanDOM
  };

  scanDOM();
})();
