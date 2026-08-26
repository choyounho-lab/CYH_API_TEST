import { useState } from 'react'
import { login, signUp } from './api/authApi.js'

const inputClassName =
  'my-2 h-13 w-[85%] rounded-[20px] border-0 bg-[#eeeeee] px-4 text-sm outline-none shadow-[inset_7px_2px_10px_#babebc,inset_-5px_-5px_12px_#fff] placeholder:text-slate-500 focus:ring-2 focus:ring-orange-500/50'

const formButtonClassName =
  'mt-5 cursor-pointer rounded-full px-12 py-4 text-xs font-extrabold uppercase tracking-widest shadow-[-5px_-5px_10px_#fff,5px_5px_8px_#babebc] transition hover:text-orange-600 active:shadow-[inset_1px_1px_2px_#babebc,inset_-1px_-1px_2px_#fff] disabled:cursor-wait disabled:opacity-60'

export default function App() {
  const [mode, setMode] = useState('login')
  const [loginId, setLoginId] = useState('')
  const [password, setPassword] = useState('')
  const [signUpForm, setSignUpForm] = useState({
    loginId: '',
    displayName: '',
    email: '',
    password: '',
  })
  const [errorMessage, setErrorMessage] = useState('')
  const [isSubmitting, setIsSubmitting] = useState(false)

  function changeMode(nextMode) {
    setMode(nextMode)
    setErrorMessage('')
  }

  function changeSignUpField(event) {
    const { name, value } = event.target
    setSignUpForm((current) => ({ ...current, [name]: value }))
  }

  async function handleLogin(event) {
    event.preventDefault()
    setErrorMessage('')
    setIsSubmitting(true)

    try {
      await login({ loginId, password })
      window.alert('로그인되었습니다.')
      setPassword('')
    } catch (error) {
      setErrorMessage(error.message)
    } finally {
      setIsSubmitting(false)
    }
  }

  async function handleSignUp(event) {
    event.preventDefault()
    setErrorMessage('')
    setIsSubmitting(true)

    try {
      const member = await signUp(signUpForm)
      window.alert('회원가입되었습니다. 로그인해주세요.')
      setLoginId(member.loginId)
      setPassword('')
      setSignUpForm({ loginId: '', displayName: '', email: '', password: '' })
      changeMode('login')
    } catch (error) {
      setErrorMessage(error.message)
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <main className="grid min-h-screen place-items-center overflow-hidden bg-[#ebecf0] px-5 py-10 text-slate-950">
      <section className={`${mode === 'signup' ? 'right-panel-active' : ''} relative min-h-[660px] w-full max-w-3xl overflow-hidden rounded-[10px] bg-[#ebecf0] shadow-[-5px_-5px_10px_#fff,5px_5px_10px_#babebc] md:min-h-[580px]`}>
        <div
          className={`sign-in-container ${mode === 'login' ? 'flex pointer-events-auto md:translate-x-0 md:opacity-100' : 'pointer-events-none hidden md:flex md:translate-x-full md:opacity-0'} items-center justify-center px-8 py-12 md:absolute md:left-0 md:top-0 md:h-full md:w-1/2 md:transition-all md:duration-500`}
        >
          <form className="flex w-full max-w-sm flex-col items-center" onSubmit={handleLogin}>
            <h1 className="text-3xl font-extrabold tracking-tight">Sign In</h1>
            <SocialLinks />
            <span className="mb-2 text-xs tracking-wide">or use your account</span>

            <label className="sr-only" htmlFor="loginId">아이디</label>
            <input
              id="loginId"
              className={inputClassName}
              type="text"
              name="loginId"
              autoComplete="username"
              placeholder="아이디"
              value={loginId}
              onChange={(event) => setLoginId(event.target.value)}
              maxLength={50}
              required
            />

            <label className="sr-only" htmlFor="password">비밀번호</label>
            <input
              id="password"
              className={inputClassName}
              type="password"
              name="password"
              autoComplete="current-password"
              placeholder="비밀번호"
              value={password}
              onChange={(event) => setPassword(event.target.value)}
              maxLength={200}
              required
            />

            {mode === 'login' && errorMessage && <ErrorMessage message={errorMessage} />}

            <button className={formButtonClassName} type="submit" disabled={isSubmitting}>
              {isSubmitting ? '로그인 중...' : 'Sign In'}
            </button>
          </form>
        </div>

        <div
          className={`sign-up-container ${mode === 'signup' ? 'flex pointer-events-auto md:translate-x-full md:opacity-100' : 'pointer-events-none hidden md:flex md:translate-x-0 md:opacity-0'} items-center justify-center px-8 py-8 md:absolute md:left-0 md:top-0 md:z-10 md:h-full md:w-1/2 md:transition-all md:duration-500`}
        >
          <form className="flex w-full max-w-sm flex-col items-center" onSubmit={handleSignUp}>
            <h1 className="text-3xl font-extrabold tracking-tight">Create Account</h1>
            <SocialLinks />
            <span className="mb-1 text-xs tracking-wide">or use your email for registration</span>

            <label className="sr-only" htmlFor="signUpLoginId">아이디</label>
            <input
              id="signUpLoginId"
              className={inputClassName}
              type="text"
              name="loginId"
              autoComplete="username"
              placeholder="아이디 (4자 이상)"
              value={signUpForm.loginId}
              onChange={changeSignUpField}
              minLength={4}
              maxLength={50}
              required
            />

            <label className="sr-only" htmlFor="displayName">이름</label>
            <input
              id="displayName"
              className={inputClassName}
              type="text"
              name="displayName"
              autoComplete="name"
              placeholder="이름"
              value={signUpForm.displayName}
              onChange={changeSignUpField}
              maxLength={100}
              required
            />

            <label className="sr-only" htmlFor="email">이메일</label>
            <input
              id="email"
              className={inputClassName}
              type="email"
              name="email"
              autoComplete="email"
              placeholder="이메일"
              value={signUpForm.email}
              onChange={changeSignUpField}
              maxLength={254}
              required
            />

            <label className="sr-only" htmlFor="signUpPassword">비밀번호</label>
            <input
              id="signUpPassword"
              className={inputClassName}
              type="password"
              name="password"
              autoComplete="new-password"
              placeholder="비밀번호 (8자 이상)"
              value={signUpForm.password}
              onChange={changeSignUpField}
              minLength={8}
              maxLength={200}
              required
            />

            {mode === 'signup' && errorMessage && <ErrorMessage message={errorMessage} />}

            <button className={formButtonClassName} type="submit" disabled={isSubmitting}>
              {isSubmitting ? '가입 중...' : 'Sign Up'}
            </button>
          </form>
        </div>

        <aside
          className={`overlay-container ${mode === 'signup' ? 'md:-translate-x-full' : 'md:translate-x-0'} absolute bottom-0 right-0 h-48 w-full overflow-hidden bg-[#ff4b2b] text-center text-white shadow-[-5px_-5px_10px_#ff6b3f,5px_5px_8px_#bf4b2b] transition-all duration-500 md:top-0 md:h-full md:w-1/2`}
        >
          <OverlayPanel
            className={`overlay-right ${mode === 'login' ? 'pointer-events-auto translate-x-0 opacity-100' : 'pointer-events-none -translate-x-full opacity-0'}`}
            title="Hello, Friend"
            description="Enter your personal details and start journey with us"
            buttonText="Sign Up"
            onClick={() => changeMode('signup')}
          />
          <OverlayPanel
            className={`overlay-left ${mode === 'signup' ? 'pointer-events-auto translate-x-0 opacity-100' : 'pointer-events-none translate-x-full opacity-0'}`}
            title="Welcome Back"
            description="To keep connected with us please login with your personal info"
            buttonText="Sign In"
            onClick={() => changeMode('login')}
          />
        </aside>
      </section>
    </main>
  )
}

function SocialLinks() {
  const links = [
    { name: 'Facebook', symbol: 'f' },
    { name: 'Twitter', symbol: 't' },
    { name: 'LinkedIn', symbol: 'in' },
  ]

  return (
    <div className="social-links my-5 flex gap-2">
      {links.map((link) => (
        <a
          key={link.name}
          className="grid h-10 w-10 place-items-center rounded-full text-black shadow-[-5px_-5px_10px_#fff,5px_5px_8px_#babebc] active:shadow-[inset_1px_1px_2px_#babebc,inset_-1px_-1px_2px_#fff]"
          href="#"
          aria-label={`${link.name} 로그인은 준비 중입니다.`}
          title={`${link.name} 로그인 준비 중`}
          onClick={(event) => event.preventDefault()}
        >
          <span className="text-sm font-black" aria-hidden="true">{link.symbol}</span>
        </a>
      ))}
    </div>
  )
}

function OverlayPanel({ className, title, description, buttonText, onClick }) {
  return (
    <div className={`${className} absolute inset-0 flex flex-col items-center justify-center px-12 transition-all duration-500`}>
      <h2 className="text-3xl font-extrabold">{title}</h2>
      <p className="my-5 max-w-xs text-sm font-semibold leading-6 tracking-wide text-white/90">
        {description}
      </p>
      <button
        className="cursor-pointer rounded-[20px] px-11 py-4 text-xs font-extrabold uppercase tracking-widest text-white shadow-[-5px_-5px_10px_#ff6b3f,5px_5px_8px_#bf4b2b] active:shadow-[inset_1px_1px_2px_#bf4b2b,inset_-1px_-1px_2px_#ff6b3f]"
        type="button"
        onClick={onClick}
      >
        {buttonText}
      </button>
    </div>
  )
}

function ErrorMessage({ message }) {
  return (
    <p className="mt-3 w-full rounded-xl bg-red-100 px-4 py-3 text-center text-sm font-semibold text-red-700" role="alert">
      {message}
    </p>
  )
}
