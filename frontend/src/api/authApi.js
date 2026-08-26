export async function login(credentials) {
  const { response, body } = await postJson('/api/auth/login', credentials)

  if (response.status === 401) {
    throw new Error('아이디 또는 비밀번호가 올바르지 않습니다.')
  }

  if (!response.ok) {
    throw new Error(body.message || '로그인 처리 중 오류가 발생했습니다.')
  }

  return body
}

export async function signUp(memberInformation) {
  const { response, body } = await postJson('/api/auth/signup', memberInformation)

  if (!response.ok) {
    throw new Error(body.message || '회원가입 처리 중 오류가 발생했습니다.')
  }

  return body
}

async function postJson(path, payload) {
  const response = await fetch(`${apiBaseUrl}${path}`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Accept: 'application/json',
    },
    body: JSON.stringify(payload),
  })

  const body = await response.json().catch(() => ({}))

  if (response.status === 404) {
    throw new Error('인증 API가 아직 준비되지 않았습니다.')
  }

  return { response, body }
}
const apiBaseUrl = (import.meta.env.VITE_API_BASE_URL || '').replace(/\/$/, '')
