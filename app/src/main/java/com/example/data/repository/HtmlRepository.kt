// [S17 AUTO-REPAIRED FOR GALAXY S17 / ONE UI 7]
package com.example.data.repository

import com.example.data.local.HtmlFile
import com.example.data.local.HtmlFileDao
import kotlinx.coroutines.flow.Flow

class HtmlRepository(private val dao: HtmlFileDao) {

    val allFiles: Flow<List<HtmlFile>> = dao.getAllFiles()

    suspend fun getFileById(id: Long): HtmlFile? = dao.getFileById(id)

    suspend fun saveFile(file: HtmlFile): Long {
        return if (file.id == 0L) {
            dao.insertFile(file)
        } else {
            dao.updateFile(file.copy(updatedAt = System.currentTimeMillis()))
            file.id
        }
    }

    suspend fun deleteFile(id: Long) = dao.deleteFileById(id)

    suspend fun checkAndSeedDefaults() {
        if (dao.getFileCount() == 0) {
            val templates = listOf(
                HtmlFile(
                    title = "Tailwind Modern Dashboard",
                    category = "Dashboards",
                    isTemplate = true,
                    content = """
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Powerhouse Dashboard</title>
  <script src="https://cdn.tailwindcss.com"></script>
  <script>
    tailwind.config = {
      darkMode: 'class',
      theme: {
        extend: {
          colors: {
            brand: { 50: '#eef2ff', 500: '#6366f1', 600: '#4f46e5', 700: '#4338ca' }
          }
        }
      }
    }
  </script>
</head>
<body class="bg-slate-900 text-slate-100 min-h-screen p-4 sm:p-6 font-sans">

  <!-- Header -->
  <header class="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 pb-6 border-b border-slate-800">
    <div>
      <div class="flex items-center gap-2">
        <span class="w-3 h-3 rounded-full bg-indigo-500 animate-pulse"></span>
        <h1 class="text-2xl font-bold tracking-tight text-white">Powerhouse Studio</h1>
      </div>
      <p class="text-sm text-slate-400 mt-1">Real-time Offline Analytics & HTML Engine</p>
    </div>
    <div class="flex gap-2">
      <button onclick="toggleActiveState()" id="statusBtn" class="px-4 py-2 bg-indigo-600 hover:bg-indigo-500 text-white text-sm font-semibold rounded-lg shadow-md transition">
        Sync Active
      </button>
      <button onclick="console.log('Console Test Log clicked!')" class="px-3 py-2 bg-slate-800 hover:bg-slate-700 text-slate-300 text-sm rounded-lg border border-slate-700">
        Test Console
      </button>
    </div>
  </header>

  <!-- Metrics Grid -->
  <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 my-6">
    <div class="bg-slate-800/80 border border-slate-700/60 p-5 rounded-xl shadow-sm">
      <span class="text-xs font-semibold text-indigo-400 uppercase tracking-wider">Total Views</span>
      <div class="text-3xl font-extrabold text-white mt-2">128,430</div>
      <div class="text-xs text-emerald-400 mt-2 font-medium">↑ +14.2% from last week</div>
    </div>
    <div class="bg-slate-800/80 border border-slate-700/60 p-5 rounded-xl shadow-sm">
      <span class="text-xs font-semibold text-purple-400 uppercase tracking-wider">Render Speed</span>
      <div class="text-3xl font-extrabold text-white mt-2">12ms</div>
      <div class="text-xs text-emerald-400 mt-2 font-medium">⚡ Ultra Low Latency</div>
    </div>
    <div class="bg-slate-800/80 border border-slate-700/60 p-5 rounded-xl shadow-sm">
      <span class="text-xs font-semibold text-sky-400 uppercase tracking-wider">Active CSS</span>
      <div class="text-3xl font-extrabold text-white mt-2">Tailwind 3.4</div>
      <div class="text-xs text-sky-400 mt-2 font-medium">Offline Bundled</div>
    </div>
    <div class="bg-slate-800/80 border border-slate-700/60 p-5 rounded-xl shadow-sm">
      <span class="text-xs font-semibold text-amber-400 uppercase tracking-wider">System Status</span>
      <div class="text-3xl font-extrabold text-amber-300 mt-2">100% Ready</div>
      <div class="text-xs text-slate-400 mt-2 font-medium">Zero Dependencies</div>
    </div>
  </div>

  <!-- Interactive Demo Card -->
  <div class="bg-gradient-to-r from-indigo-900/40 to-purple-900/40 border border-indigo-500/30 rounded-2xl p-6 shadow-xl">
    <h2 class="text-xl font-bold text-white mb-2">Live Interactive State</h2>
    <p class="text-slate-300 text-sm mb-4">Edit this HTML code on the left split panel and watch the preview update instantly!</p>
    <div id="statusAlert" class="p-4 bg-indigo-950/80 border border-indigo-500/40 rounded-xl text-indigo-200 text-sm font-mono">
      Status: Ready for high-performance offline editing.
    </div>
  </div>

  <script>
    let active = true;
    function toggleActiveState() {
      active = !active;
      const btn = document.getElementById('statusBtn');
      const alert = document.getElementById('statusAlert');
      if (active) {
        btn.innerText = 'Sync Active';
        btn.className = 'px-4 py-2 bg-indigo-600 hover:bg-indigo-500 text-white text-sm font-semibold rounded-lg shadow-md transition';
        alert.innerText = 'Status: Ready for high-performance offline editing.';
        console.log('System state set to ACTIVE');
      } else {
        btn.innerText = 'Sync Paused';
        btn.className = 'px-4 py-2 bg-amber-600 hover:bg-amber-500 text-white text-sm font-semibold rounded-lg shadow-md transition';
        alert.innerText = 'Status: Sync temporarily paused.';
        console.warn('System state set to PAUSED');
      }
    }
  </script>
</body>
</html>
                    """.trimIndent()
                ),
                HtmlFile(
                    title = "Tailwind Hero & SaaS Page",
                    category = "Landing Pages",
                    isTemplate = true,
                    content = """
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Aura SaaS</title>
  <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-slate-950 text-slate-100 font-sans antialiased">
  <div class="max-w-4xl mx-auto px-6 py-12">
    <!-- Navbar -->
    <nav class="flex justify-between items-center mb-16">
      <div class="text-xl font-extrabold bg-gradient-to-r from-cyan-400 to-blue-500 bg-clip-text text-transparent">
        AURA.IO
      </div>
      <div class="flex gap-4">
        <button class="text-sm text-slate-400 hover:text-white transition">Docs</button>
        <button class="text-sm px-4 py-2 bg-cyan-500 hover:bg-cyan-400 text-slate-950 font-bold rounded-full transition">Get Started</button>
      </div>
    </nav>

    <!-- Hero section -->
    <div class="text-center py-10">
      <span class="inline-block px-3 py-1 bg-cyan-950 text-cyan-400 text-xs font-semibold rounded-full border border-cyan-800/50 mb-4">
        ✨ Powered by Offline Tailwind CSS
      </span>
      <h1 class="text-4xl sm:text-6xl font-black tracking-tight text-white mb-6">
        Build stunning web layouts <br>
        <span class="bg-gradient-to-r from-cyan-400 via-blue-500 to-indigo-500 bg-clip-text text-transparent">
          Right on your phone
        </span>
      </h1>
      <p class="text-slate-400 text-base sm:text-lg max-w-2xl mx-auto mb-8">
        An ultra-fast, powerhouse HTML editor and previewer equipped with line highlighting, split screen, JS console, and pre-bundled Tailwind engine.
      </p>

      <div class="flex flex-wrap justify-center gap-4">
        <button onclick="alert('Hero CTA clicked!')" class="px-6 py-3 bg-gradient-to-r from-cyan-500 to-blue-600 hover:from-cyan-400 hover:to-blue-500 text-slate-950 font-bold text-sm rounded-xl shadow-lg transition">
          Launch Editor
        </button>
        <button onclick="console.log('Documentation requested')" class="px-6 py-3 bg-slate-900 border border-slate-800 text-slate-300 font-semibold text-sm rounded-xl hover:bg-slate-800 transition">
          Explore Code
        </button>
      </div>
    </div>
  </div>
</body>
</html>
                    """.trimIndent()
                ),
                HtmlFile(
                    title = "Interactive Glass Calculator",
                    category = "Widgets",
                    isTemplate = true,
                    content = """
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Glassmorphism Calculator</title>
  <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-gradient-to-br from-indigo-900 via-slate-900 to-black min-h-screen flex items-center justify-center p-4">
  <div class="w-full max-w-xs bg-white/10 backdrop-blur-md border border-white/20 p-6 rounded-3xl shadow-2xl">
    <div class="mb-4">
      <div id="expression" class="text-right text-xs text-indigo-300 h-4"></div>
      <div id="display" class="text-right text-3xl font-mono font-bold text-white tracking-wider truncate">0</div>
    </div>

    <div class="grid grid-cols-4 gap-3">
      <button onclick="clearDisplay()" class="col-span-2 py-3 bg-rose-500/80 hover:bg-rose-500 text-white font-bold rounded-2xl transition">C</button>
      <button onclick="append('/')" class="py-3 bg-indigo-600/80 hover:bg-indigo-600 text-white font-bold rounded-2xl">÷</button>
      <button onclick="append('*')" class="py-3 bg-indigo-600/80 hover:bg-indigo-600 text-white font-bold rounded-2xl">×</button>

      <button onclick="append('7')" class="py-3 bg-white/10 hover:bg-white/20 text-white font-semibold rounded-2xl">7</button>
      <button onclick="append('8')" class="py-3 bg-white/10 hover:bg-white/20 text-white font-semibold rounded-2xl">8</button>
      <button onclick="append('9')" class="py-3 bg-white/10 hover:bg-white/20 text-white font-semibold rounded-2xl">9</button>
      <button onclick="append('-')" class="py-3 bg-indigo-600/80 hover:bg-indigo-600 text-white font-bold rounded-2xl">-</button>

      <button onclick="append('4')" class="py-3 bg-white/10 hover:bg-white/20 text-white font-semibold rounded-2xl">4</button>
      <button onclick="append('5')" class="py-3 bg-white/10 hover:bg-white/20 text-white font-semibold rounded-2xl">5</button>
      <button onclick="append('6')" class="py-3 bg-white/10 hover:bg-white/20 text-white font-semibold rounded-2xl">6</button>
      <button onclick="append('+')" class="py-3 bg-indigo-600/80 hover:bg-indigo-600 text-white font-bold rounded-2xl">+</button>

      <button onclick="append('1')" class="py-3 bg-white/10 hover:bg-white/20 text-white font-semibold rounded-2xl">1</button>
      <button onclick="append('2')" class="py-3 bg-white/10 hover:bg-white/20 text-white font-semibold rounded-2xl">2</button>
      <button onclick="append('3')" class="py-3 bg-white/10 hover:bg-white/20 text-white font-semibold rounded-2xl">3</button>
      <button onclick="calculate()" class="row-span-2 bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-bold rounded-2xl text-xl flex items-center justify-center">=</button>

      <button onclick="append('0')" class="col-span-2 py-3 bg-white/10 hover:bg-white/20 text-white font-semibold rounded-2xl">0</button>
      <button onclick="append('.')" class="py-3 bg-white/10 hover:bg-white/20 text-white font-semibold rounded-2xl">.</button>
    </div>
  </div>

  <script>
    let currentInput = '0';
    const display = document.getElementById('display');
    
    function append(val) {
      if (currentInput === '0' && val !== '.') {
        currentInput = val;
      } else {
        currentInput += val;
      }
      display.innerText = currentInput;
    }

    function clearDisplay() {
      currentInput = '0';
      display.innerText = '0';
      console.log('Calculator cleared');
    }

    function calculate() {
      try {
        const result = eval(currentInput);
        console.log('Calculated:', currentInput, '=', result);
        currentInput = String(result);
        display.innerText = currentInput;
      } catch (e) {
        console.error('Calculation error:', e);
        display.innerText = 'Error';
        currentInput = '0';
      }
    }
  </script>
</body>
</html>
                    """.trimIndent()
                )
            )

            for (template in templates) {
                dao.insertFile(template)
            }
        }
    }
}
